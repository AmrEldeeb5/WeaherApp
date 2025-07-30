package com.example.weatherforecast.data

import androidx.room.*
import com.example.weatherforecast.model.Unit
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM unit_table")
    fun getUnits(): Flow<List<Unit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit)

    @Update
    suspend fun updateUnit(unit: Unit)

    @Delete
    suspend fun deleteUnit(unit: Unit)

    @Query("DELETE FROM unit_table")
    suspend fun deleteAllUnits()
}

