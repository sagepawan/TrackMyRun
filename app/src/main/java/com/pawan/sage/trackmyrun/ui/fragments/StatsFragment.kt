package com.pawan.sage.trackmyrun.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.FragmentStatsBinding
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import com.pawan.sage.trackmyrun.ui.viewmodels.StatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatsFragment : Fragment() {

    //to inject viewModel from dagger here
    private val viewModel: StatViewModel by viewModels()
    lateinit var binding: FragmentStatsBinding
    lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        subscribeToObservers()
        barChart = binding.barChart
        return binding.root
    }

    private fun setupBarChart(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM

            //to click on bar and display details on click
            setDrawLabels(false)

            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.apply {
            description.text = "Average Speed Over Time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let{
                val totalTimeRun = TrackingUtility.getStopWatchTimeInFormat(it)
                binding.tvTotalTime.text = totalTimeRun
                val totalDistanceString = "$(totalDistance)"
            }
        })


        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let{
                val km = it / 1000f
                val totalDistanceRun = round(km*10f)/10f
                val totalDistanceString = "${totalDistanceRun}km"
                binding.tvTotalDistance.text = totalDistanceString

            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let{
                val avgSpeed = round(it*10f)/10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let{
                val caloriesTotal = "${it}Kcal"
                binding.tvTotalCalories.text = caloriesTotal
            }
        })

        //live data observable to fetch data for bar chart
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let{
                //x value for bar will be chronological order count, y value will be respective avg speed
                val allAvgSpeeds = it.indices.map{i -> BarEntry(i.toFloat(), it[i].averageSpeedKMPH)}

                //creating bar data set using bar entries
                val barDataSet = BarDataSet(allAvgSpeeds, "Average Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }

                barChart.data = BarData(barDataSet)

                //creating marker view to pop up custom view when bar is clicked
                barChart.invalidate()
            }
        })

    }
}