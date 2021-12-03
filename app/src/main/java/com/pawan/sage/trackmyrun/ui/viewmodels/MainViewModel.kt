package com.pawan.sage.trackmyrun.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.pawan.sage.trackmyrun.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
}