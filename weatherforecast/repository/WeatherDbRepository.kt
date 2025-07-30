package com.example.weatherforecast.repository

import com.example.weatherforecast.data.WeatherDao
import com.example.weatherforecast.model.Unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class WeatherDbRepository(private val dao: WeatherDao) {
    fun getUnits(): Flow<List<Unit>> = dao.getUnits().flowOn(Dispatchers.IO)

    suspend fun insertUnit(unit: Unit) = dao.insertUnit(unit)

    suspend fun updateUnit(unit: Unit) = dao.updateUnit(unit)

    suspend fun deleteUnit(unit: Unit) = dao.deleteUnit(unit)

    suspend fun deleteAllUnits() = dao.deleteAllUnits()
}
