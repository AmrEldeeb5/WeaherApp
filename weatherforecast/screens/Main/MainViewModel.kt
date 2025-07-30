package com.example.weatherforecast.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.DataOrException
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableStateFlow<DataOrException<Weather, Boolean, Exception>>(DataOrException(loading = true))
    val weatherData = _weatherData.asStateFlow()

    fun getWeatherData(city: String, units: String) {
        viewModelScope.launch {
            _weatherData.value = DataOrException(loading = true) // Set loading state
            _weatherData.value = repository.getWeather(cityQuery = city, units = units)
        }
    }
}
