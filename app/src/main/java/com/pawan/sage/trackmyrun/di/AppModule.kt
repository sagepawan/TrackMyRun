package com.pawan.sage.trackmyrun.di

import android.content.Context
import androidx.room.Room
import com.pawan.sage.trackmyrun.db.RunDatabase
import com.pawan.sage.trackmyrun.otherpackages.Constants.RUN_DATABASE_VALUE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

//Module class for object injection
@Module
@InstallIn(ApplicationComponentManager::class)
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
}