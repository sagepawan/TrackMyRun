package com.pawan.sage.trackmyrun.repositories

import com.pawan.sage.trackmyrun.db.Run
import com.pawan.sage.trackmyrun.db.RunDao
import javax.inject.Inject

//to provide functions of db for view models
//Repository to collect all data from DB
class MainRepository @Inject constructor(
    val runDao: RunDao
){
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    //returns livedata which is asynchronous already so functions below don't need to be suspend
    fun getAllRunSortedByDate() = runDao.getRunsSortedByDate()

    fun getAllRunSortedByDistance() = runDao.getRunsSortedByDistance()

    fun getAllRunSortedByDuration() = runDao.getRunsSortedByDuration()

    fun getAllRunSortedByAvgPace() = runDao.getRunsSortedByAvgPace()

    fun getAllRunSortedByCaloriesSpent() = runDao.getRunsSortedByCaloriesSpent()

    fun getTotalAvgPace() = runDao.getTotalAvgPace()

    fun getTotalDistance() = runDao.getTotalDistanceRun()

    fun getTotalCaloriesSpent() = runDao.getTotalCaloriesSpent()

    fun getTotalTimeRunMillis() = runDao.getTotalRunningTimeInMillis()


}