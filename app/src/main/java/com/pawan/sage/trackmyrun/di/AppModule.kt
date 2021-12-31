package com.pawan.sage.trackmyrun.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.pawan.sage.trackmyrun.db.RunDatabase
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_FIRST_TIME_TOGGLE
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_NAME
import com.pawan.sage.trackmyrun.otherpackages.Constants.KEY_WEIGHT
import com.pawan.sage.trackmyrun.otherpackages.Constants.RUN_DATABASE_VALUE
import com.pawan.sage.trackmyrun.otherpackages.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Module class for object injection
@Module
@InstallIn(SingletonComponent::class)
//scope of app module is defined for the lifetime of application itself
//other components that can be used - Activity, Service, Fragment
object AppModule {

    @Singleton //each class that needs the run db will get same instance
    @Provides
    fun provideRunDB(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunDatabase::class.java,
        RUN_DATABASE_VALUE
    ).build()

    //to provide dao object
    @Singleton
    @Provides
    fun provideRunDao(db: RunDatabase) = db.getRunDao()

    //setup shared prefs to store user weight and age in setup fragment
    //idea is to use this to show setup fragment only on the first run of app

    @Singleton
    @Provides
    fun providesSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)!!

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

}