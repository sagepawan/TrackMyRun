package com.pawan.sage.trackmyrun.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.sage.trackmyrun.db.Run
import com.pawan.sage.trackmyrun.otherpackages.SortType
import com.pawan.sage.trackmyrun.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    private val runsSortedByDate = mainRepository.getAllRunSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunSortedByDistance()
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunSortedByCaloriesSpent()
    private val runsSortedByTime = mainRepository.getAllRunSortedByDuration()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunSortedByAvgPace()

    //use mediator live data to provide relevant data based on choice made by spinner in Runs
    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        //live data added as source to runs mediator live data
        runs.addSource(runsSortedByDate){ result ->
            //lambda function used to check sort type
            if(sortType == SortType.DATE){
                result?.let {
                    //set current value to the result of observable
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSortedByDistance){ result ->
            //lambda function used to check sort type
            if(sortType == SortType.DISTANCE){
                result?.let {
                    //set current value to the result of observable
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSortedByCaloriesBurned){ result ->
            //lambda function used to check sort type
            if(sortType == SortType.CALORIES_BURNED){
                result?.let {
                    //set current value to the result of observable
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSortedByTime){ result ->
            //lambda function used to check sort type
            if(sortType == SortType.RUNNING_TIME){
                result?.let {
                    //set current value to the result of observable
                    runs.value = it
                }
            }
        }

        runs.addSource(runsSortedByAvgSpeed){ result ->
            //lambda function used to check sort type
            if(sortType == SortType.AVG_SPEED){
                result?.let {
                    //set current value to the result of observable
                    runs.value = it
                }
            }
        }

    }

    //function to receive sort type changes
    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTime.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
    }.also{
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}