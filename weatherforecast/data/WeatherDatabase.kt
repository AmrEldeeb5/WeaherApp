package com.example.weatherforecast.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.Unit

@Database(entities = [Unit::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}

