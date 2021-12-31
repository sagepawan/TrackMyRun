package com.pawan.sage.trackmyrun.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentTrackingBinding
import com.pawan.sage.trackmyrun.db.Run
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_PAUSE_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_START_RESUME_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_STOP_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.MAP_ZOOM
import com.pawan.sage.trackmyrun.otherpackages.Constants.POLYLINE_WIDTH
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import com.pawan.sage.trackmyrun.service.PolyLine
import com.pawan.sage.trackmyrun.service.TrackingService
import com.pawan.sage.trackmyrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    //to inject viewModel from dagger here
    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: FragmentTrackingBinding

    private var map: GoogleMap ?= null //base map object

    private var isTracking = false
    private var pathPoints = mutableListOf<PolyLine>()

    lateinit var btnToggleRun: Button
    lateinit var btnFinishRun: Button

    private var currentTimeInMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTrackingBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync{
            map = it
            addAllPolylines()
        }

        btnToggleRun = binding.btnToggleRun
        btnFinishRun = binding.btnFinishRun

        btnToggleRun.setOnClickListener{
            toggleRun()
        }

        btnFinishRun.setOnClickListener{
            zoomOutEntireRunningTrack()
            finishRunAndSaveToLocalDb()
        }

        subscribeToObservers()


        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("menuInflater onCreate", menu.toString())
        setHasOptionsMenu(true)
    }

    //to maintain polyline when state is changed, using livedata
     private fun addAllPolylines(){
         for(polyline in pathPoints){
             val polylineOptions = PolylineOptions()
                 .color(Color.RED)
                 .width(POLYLINE_WIDTH)
                 .addAll(polyline)
             map?.addPolyline(polylineOptions)
         }
     }

    //function to take camera to user's latest position on new location update in polyline list
    private fun panCameraOnUserPosition(){
        if(pathPoints.isNotEmpty()&&pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("menuInflater called", menu.toString())
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
        //super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if(currentTimeInMillis > 0L){
            //since we only have one menu item
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menuItemCancelTracking -> {
                cancelRunDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun cancelRunDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the run?")
            .setMessage("Are you sure you want to cancel current run? This will delete all its track.")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_, _ ->
                finishRun()
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        dialog.show()
    }

    private fun finishRun(){
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    //fun to subscribe to live data objects
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer{
            pathPoints = it
            addLastPolyline()
            panCameraOnUserPosition()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getStopWatchTimeInFormat(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_RESUME_SERVICE)
        }
    }

    //fun to observe data from servers (local db) and react to those changes
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            btnFinishRun.visibility = View.GONE
        }
    }

    //function to draw polyline
    //Idea is to connect last two coordinates for each new location update
    private fun addLastPolyline(){
        if(pathPoints.isNotEmpty()&&pathPoints.last().size>1){
            val preLastCoord = pathPoints.last()[pathPoints.last().size-2]  //second last point
            val lastCoord = pathPoints.last().last()  //last point
            val polylineOptions = PolylineOptions()
                .color(Color.RED)
                .width(POLYLINE_WIDTH)
                .add(preLastCoord)
                .add(lastCoord)
            map?.addPolyline(polylineOptions)
        }
    }

    //to communicate with service
    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    //to cache the map
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        binding.mapView?.onSaveInstanceState(outState)
    }

    //function that allows us to zoom out in the map such that all polylines are included within a single screen
    private fun zoomOutEntireRunningTrack() {
        //define latlng bounds
        val bounds = LatLngBounds.Builder()
        var counter = 0
        for(polyline in pathPoints){
            for(position in polyline){
                counter++
                bounds.include(position)
            }
        }
        Log.d("counterValue is ", counter.toString())

        if(counter>0) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    binding.mapView.width,
                    binding.mapView.height,
                    (binding.mapView.height * 0.05f).toInt()
                )
            )

        }
    }

    //finish run and save run image to local db once screen zooms to entire run in mapview
    private fun finishRunAndSaveToLocalDb() {
        map?.snapshot { bitmap ->
            var distanceInMeters = 0

            for(polyline in pathPoints){
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }

            val averageSpeedInKMS = round((distanceInMeters / 1000f)/(currentTimeInMillis/1000f/60/60)*10)/10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters/1000f)*weight).toInt()

            val run = Run(
                bitmap,
                dateTimestamp,
                averageSpeedInKMS,
                distanceInMeters,
                currentTimeInMillis,
                caloriesBurned)

            viewModel.insertRun(run)

            Snackbar.make(
                //since we are navigating back to run fragment at this point
                requireActivity().findViewById(R.id.rootView) ,
                "Run saved successfully!",
                Snackbar.LENGTH_LONG
            ).show()

            finishRun()
        }
    }

}