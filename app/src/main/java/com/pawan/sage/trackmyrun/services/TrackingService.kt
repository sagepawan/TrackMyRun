package com.pawan.sage.trackmyrun.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.pawan.sage.trackmyrun.MainActivity
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_PAUSE_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_START_RESUME_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_STOP_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.NOTIFICATION_CHANNEL_ID
import com.pawan.sage.trackmyrun.otherpackages.Constants.NOTIFICATION_CHANNEL_NAME
import com.pawan.sage.trackmyrun.otherpackages.Constants.NOTIFICATION_ID
import timber.log.Timber

typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>

class TrackingService : LifecycleService() {

    var isFirstRun = true

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        //live data to hold tracked locations for specific runs
        //list of coordinates used to draw lines, list of polylines
        //val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()
        val pathPoints = MutableLiveData<PolyLines>()
    }

    //to pull initial values into the livedata polyline object
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue((mutableListOf()))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_RESUME_SERVICE -> {
                    Timber.d("Service started or resumed")
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Service resumed")
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Service paused")
                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("Service stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //function to add coordinate to the last polyline at end of polyline list
    private fun addPathCoordinate(location : Location?){
        location?.let{
            val position = LatLng(location.latitude, location.longitude)
            //grab the last value in pathpoints and add new coordinate to it
            pathPoints.value?.apply{
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    //function to add empty polyline at the end of polyline list
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService(){

        addEmptyPolyline()

        //system service of android framework used for notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("Running Tracker")
            .setContentText("00:00:00")

        //start foreground service
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    //to setup flow to tracking fragment when notification is clicked
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    //to setup foreground service
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}