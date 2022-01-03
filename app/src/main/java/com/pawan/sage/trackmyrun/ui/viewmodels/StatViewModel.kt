package com.pawan.sage.trackmyrun.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.pawan.sage.trackmyrun.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    val totalTimeRun = mainRepository.getTotalTimeRunMillis()
    val totalDistance = mainRepository.getTotalDistance()
    val totalCaloriesBurned = mainRepository.getTotalCaloriesSpent()
    val totalAvgSpeed = mainRepository.getTotalAvgPace()

    val runsSortedByDate = mainRepository.getAllRunSortedByDate()
}