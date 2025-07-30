package com.example.weatherforecast.repository

import com.example.weatherforecast.model.Unit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class WeatherDbRepository {
    private val units = MutableStateFlow<List<Unit>>(emptyList())

    fun getUnits(): Flow<List<Unit>> = units

    // Removed redundant 'suspend' modifier
    fun insertUnit(unit: Unit) {
        units.value = listOf(unit)
    }

    fun updateUnit(unit: Unit) {
        units.value = listOf(unit)
    }

    // Removed unused parameter 'unit' and redundant 'suspend' modifier
    fun deleteUnit(unit: Unit) {
        units.value = emptyList()
    }

    fun deleteAllUnits() {
        units.value = emptyList()
    }
}
