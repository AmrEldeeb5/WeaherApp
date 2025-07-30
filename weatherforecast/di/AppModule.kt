package com.example.weatherforecast.di

import com.example.weatherforecast.network.WeatherApi
import com.example.weatherforecast.repository.WeatherRepository
import com.example.weatherforecast.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Network Providers ---

    @Singleton
    @Provides
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    // Note: You likely have two repositories, one for network and one for DB.
    // Hilt can now create WeatherDbRepository automatically because it knows how to get a WeatherDao.
    // This provider is for your network repository.
    @Singleton
    @Provides
    fun provideWeatherRepository(api: WeatherApi): WeatherRepository = WeatherRepository(api)

}
