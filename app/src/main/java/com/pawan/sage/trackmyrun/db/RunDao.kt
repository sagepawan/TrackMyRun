package com.pawan.sage.trackmyrun.db

import androidx.lifecycle.LiveData
import androidx.room.*

//to add all data access object functionalities to Run DB
@Dao
interface RunDao {
    //to insert item, when conflict replace data item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    //to access running data based on available parameters
    @Query("SELECT * FROM run_table ORDER BY timeStampRun DESC")
    fun getRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY runTimeMillis DESC")
    fun getRunsSortedByDuration(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY averageSpeedKMPH DESC")
    fun getRunsSortedByAvgPace(): LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY caloriesSpent DESC")
    fun getRunsSortedByCaloriesSpent(): LiveData<List<Run>>

    //to access sum and average of various parameters
    @Query("SELECT SUM(runTimeMillis) FROM run_table")
    fun getTotalRunningTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesSpent) FROM run_table")
    fun getTotalCaloriesSpent(): LiveData<Int>

    @Query("SELECT SUM(distanceRunMeters) FROM run_table")
    fun getTotalDistanceRun(): LiveData<Int>

    @Query("SELECT AVG(averageSpeedKMPH) FROM run_table")
    fun getTotalAvgPace(): LiveData<Float>
}