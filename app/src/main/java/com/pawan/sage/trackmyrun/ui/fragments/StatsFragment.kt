package com.pawan.sage.trackmyrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.ui.viewmodels.StatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    //to inject viewModel from dagger here
    private val viewModel: StatViewModel by viewModels()
}