package com.example.weatherforecast.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.Unit
import com.example.weatherforecast.repository.WeatherDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: WeatherDbRepository
) : ViewModel() {

    // Private mutable state flow to hold the list of units
    private val _unitList = MutableStateFlow<List<Unit>>(emptyList())
    // Public immutable state flow for the UI to observe
    val unitList = _unitList.asStateFlow()

    // The init block is executed when the ViewModel is created.
    // It launches a coroutine to fetch the units from the database.
    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Collects the flow from the repository and updates the state flow.
            // distinctUntilChanged() ensures that the flow only emits when the data has actually changed.
            repository.getUnits().distinctUntilChanged().collect { listOfUnits ->
                if (listOfUnits.isNullOrEmpty()) {
                    Log.d("SettingsViewModel", "No units found in the database.")
                } else {
                    _unitList.value = listOfUnits
                    Log.d("SettingsViewModel", "Loaded units: ${listOfUnits[0].unit}")
                }
            }
        }
    }

    /**
     * Inserts a new unit into the database.
     * @param unit The Unit object to insert.
     */
    fun insertUnit(unit: Unit) = viewModelScope.launch {
        repository.insertUnit(unit)
    }

    /**
     * Updates an existing unit in the database.
     * @param unit The Unit object to update.
     */
    fun updateUnit(unit: Unit) = viewModelScope.launch {
        repository.updateUnit(unit)
    }

    /**
     * Deletes a unit from the database.
     * @param unit The Unit object to delete.
     */
    fun deleteUnit(unit: Unit) = viewModelScope.launch {
        repository.deleteUnit(unit)
    }

    /**
     * Deletes all units from the database.
     * Useful for resetting settings.
     */
    fun deleteAllUnits() = viewModelScope.launch {
        repository.deleteAllUnits()
    }
}
