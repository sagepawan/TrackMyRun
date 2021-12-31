package com.pawan.sage.trackmyrun.otherpackages

import androidx.compose.ui.graphics.Color

object Constants {

    const val RUN_DATABASE_VALUE = "run_db"
    const val REQ_CODE_LOCATION_PERMISSION = 0

    const val ACTION_START_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val TIMER_UPDATE_INTERVAL = 50L

    const val LOCATION_UPDATE_INTERVAL = 4500L
    const val LOCATION_UPDATE_FASTEST_INTERVAL = 2000L

    const val SHARED_PREFERENCES_NAME = "sharedprefs"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"

    //const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 7f
    const val MAP_ZOOM = 14f

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "TRACKING"
    const val NOTIFICATION_ID = 1
}