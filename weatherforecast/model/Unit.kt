package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_table")
data class Unit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val unit: String
)
