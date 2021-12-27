package com.pawan.sage.trackmyrun.otherpackages

import android.content.Context
import android.location.Location
import android.os.Build
import com.pawan.sage.trackmyrun.service.PolyLine
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun checkHasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun getStopWatchTimeInFormat(millis: Long, includeMillis: Boolean = false): String {
        var milliseconds = millis

        //hours used in display
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes((milliseconds))
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)


        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                if (includeMillis) "${if (milliseconds < 10) "0" else ""}$milliseconds" else ""

    }

    fun calculatePolylineLength(polyline: PolyLine): Float{
        var distance = 0f
        //loop leading upto size-2 since the highest index will be at i+1
        for(i in 0..polyline.size-2){
            val position1 = polyline[i]
            val position2 = polyline[i+1]

            val result = FloatArray(1)

            Location.distanceBetween(
                position1.latitude,
                position1.longitude,
                position2.latitude,
                position2.longitude,
                result
            )

            distance += result[0]

        }

        return  distance
    }
}