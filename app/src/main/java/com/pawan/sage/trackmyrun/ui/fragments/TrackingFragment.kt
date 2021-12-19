package com.pawan.sage.trackmyrun.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentTrackingBinding
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_PAUSE_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_START_RESUME_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.MAP_ZOOM
import com.pawan.sage.trackmyrun.otherpackages.Constants.POLYLINE_WIDTH
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import com.pawan.sage.trackmyrun.service.PolyLine
import com.pawan.sage.trackmyrun.service.TrackingService
import com.pawan.sage.trackmyrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        subscribeToObservers()

        return binding.root
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

}