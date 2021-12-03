package com.pawan.sage.trackmyrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    //to inject viewModel from dagger here
    private val viewModel: MainViewModel by viewModels()
}