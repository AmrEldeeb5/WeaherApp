package com.example.weatherforecast.di

import android.content.Context
import androidx.room.Room
import com.example.weatherforecast.data.WeatherDao
import com.example.weatherforecast.data.WeatherDatabase
import com.example.weatherforecast.repository.WeatherDbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideWeatherDao(weatherDatabase: WeatherDatabase): WeatherDao =
        weatherDatabase.weatherDao()

    @Singleton
    @Provides
    fun provideDbRepository(dao: WeatherDao) = WeatherDbRepository(dao)
}
