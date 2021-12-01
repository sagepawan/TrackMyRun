package com.pawan.sage.trackmyrun

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

//Setup to make application use dagger hilt as dependency injection tool
@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree()) //to enable debug at application level using timber library
    }
}