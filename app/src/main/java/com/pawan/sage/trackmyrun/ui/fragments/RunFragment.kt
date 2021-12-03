package com.pawan.sage.trackmyrun.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    //to inject viewModel from dagger here
    private val viewModel: MainViewModel by viewModels()

}