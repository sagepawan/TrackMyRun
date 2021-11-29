package com.pawan.sage.trackmyrun.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(BitmapConverters::class) //to force compile to use create converters for bitmap for this db
abstract class RunDatabase: RoomDatabase() {

    abstract fun getRunDao(): RunDao
}