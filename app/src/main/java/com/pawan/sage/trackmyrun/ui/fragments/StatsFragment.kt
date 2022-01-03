package com.pawan.sage.trackmyrun.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentStatsBinding
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import com.pawan.sage.trackmyrun.ui.viewmodels.StatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment() {

    //to inject viewModel from dagger here
    private val viewModel: StatViewModel by viewModels()
    lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalTimeRun = TrackingUtility.getStopWatchTimeInFormat(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
    }
}