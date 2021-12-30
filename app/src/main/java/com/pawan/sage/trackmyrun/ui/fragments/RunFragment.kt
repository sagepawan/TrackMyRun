package com.pawan.sage.trackmyrun.ui.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.adapters.RunAdapter
import com.pawan.sage.trackmyrun.databinding.FragmentRunBinding
import com.pawan.sage.trackmyrun.otherpackages.SortType
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import com.pawan.sage.trackmyrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    //to inject viewModel from dagger here
    private val viewModel: MainViewModel by viewModels()

    private lateinit var runAdapter: RunAdapter

    private lateinit var binding: FragmentRunBinding

    private var isCoarseLocationPermissionGranted = false
    private var isFineLocationPermissionGranted = false
    private var isBackgroundLocationPermissionGranted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRunBinding.inflate(inflater, container, false)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        requestPermissions()
        setupRecyclerView()

        when(viewModel.sortType){
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.DISTANCE -> binding.spFilter.setSelection(2)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
               when(pos){
                   0 -> viewModel.sortRuns(SortType.DATE)
                   1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                   2 -> viewModel.sortRuns(SortType.DISTANCE)
                   3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                   4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
               }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.runs.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            runAdapter.submitList(it)
        })

        return binding.root

    }

    private fun setupRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermissions() {
        if (TrackingUtility.checkHasLocationPermissions(requireContext())) {
            return
        }

        requestPermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        )

    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            isCoarseLocationPermissionGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: isCoarseLocationPermissionGranted
            isFineLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                ?: isFineLocationPermissionGranted
            isBackgroundLocationPermissionGranted = permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION]
                ?: isBackgroundLocationPermissionGranted
        }

}