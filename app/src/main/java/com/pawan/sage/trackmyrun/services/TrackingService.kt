package com.pawan.sage.trackmyrun.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.pawan.sage.trackmyrun.MainActivity
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_PAUSE_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_START_RESUME_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.ACTION_STOP_SERVICE
import com.pawan.sage.trackmyrun.otherpackages.Constants.LOCATION_UPDATE_FASTEST_INTERVAL
import com.pawan.sage.trackmyrun.otherpackages.Constants.LOCATION_UPDATE_INTERVAL
import com.pawan.sage.trackmyrun.otherpackages.Constants.NOTIFICATION_CHANNEL_ID
import com.pawan.sage.trackmyrun.otherpackages.Constants.NOTIFICATION_CHANNEL_NAME
import com.pawan.sage.trackmyrun.otherpackages.Constants.NOTIFICATION_ID
import com.pawan.sage.trackmyrun.otherpackages.Constants.TIMER_UPDATE_INTERVAL
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    //live data for running time for user in seconds
    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {

        val timeRunInMillis = MutableLiveData<Long>()

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
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()

        currentNotificationBuilder = baseNotificationBuilder

        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer{
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    //to toggle between notification state for pause and resume
    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //to ensure only pause/resume action are shown as action
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true

            //assign action value to empty list
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        currentNotificationBuilder = baseNotificationBuilder
            .addAction(R.drawable.ic_drawable_black_pause, notificationActionText, pendingIntent)
        notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
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
                        startTimer()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    pauseService()
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
    
    //define location callback using FusedLocationProviderClient, this will grab new location updates
    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if(isTracking.value!!) {
                locationResult?.locations?.let { locations ->
                    for(location in locations){
                        addPathCoordinate(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private var isTimerEnabled = false
    private var lapTime = 0L //current ongoing lap time
    private var totalTimeRun = 0L //summation of all lap times
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    //fun to track time and trigger observers for livedata
    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        //use of coroutines to track/stop current time
        //this avoids calling observers constantly which will be heavy on system resources

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //current time to the time started difference
                lapTime = System.currentTimeMillis() - timeStarted
                //posting new lap time
                timeRunInMillis.postValue(totalTimeRun+lapTime)

                if(timeRunInMillis.value!! >= lastSecondTimeStamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            totalTimeRun += lapTime
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        if(isTracking){
            if(TrackingUtility.checkHasLocationPermissions(this)){
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = LOCATION_UPDATE_FASTEST_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else { 
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    //function to add empty polyline at the end of polyline list
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService(){

        startTimer()

        isTracking.postValue(true)

        //system service of android framework used for notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        //start foreground service
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            val notification = currentNotificationBuilder
                .setContentText(TrackingUtility.getStopWatchTimeInFormat(it * 1000L))
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        })
    }

    //to setup flow to tracking fragment when notification is clicked
    /*private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )*/

    //to setup foreground service
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}