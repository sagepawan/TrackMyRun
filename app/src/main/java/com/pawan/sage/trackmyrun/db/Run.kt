package com.pawan.sage.trackmyrun.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class Run(
    var img: Bitmap? = null,
    var timeStampRun: Long = 0L,       //when run was taken
    var averageSpeedKMPH: Float = 0f,
    var distanceRunMeters: Int = 0,
    var runTimeMillis: Long = 0L,        //duration of run
    var caloriesSpent: Int = 0

) {
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}