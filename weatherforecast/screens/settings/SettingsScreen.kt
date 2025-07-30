package com.example.weatherforecast.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.model.Unit
import com.example.weatherforecast.widgets.WeatherAppBar

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    // State for the currently selected unit choice in the UI
    var unitToggleState by remember { mutableStateOf(false) }

    // Collect the list of units from the ViewModel
    val measurementUnits = settingsViewModel.unitList.collectAsState().value

    // Determine the default choice from the database, if it exists
    val defaultChoice = if (measurementUnits.isNotEmpty()) {
        measurementUnits[0].unit
    } else {
        "Imperial (F)" // Default to Imperial if nothing is in the DB
    }

    // State to hold the final selected unit string
    var choiceState by remember { mutableStateOf(defaultChoice) }

    // Update the toggle state based on the choice
    // This ensures the UI reflects the saved setting on screen entry
    LaunchedEffect(choiceState) {
        unitToggleState = choiceState == "Imperial (F)"
    }

    Scaffold(
        topBar = {
            WeatherAppBar(
                title = "Settings",
                icon = Icons.Default.ArrowBack,
                isMainScreen = false,
                navController = navController
            ) {
                navController.popBackStack()
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Change Units of Measurement",
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.h6
                )

                // The toggle button for selecting Metric or Imperial
                IconToggleButton(
                    checked = unitToggleState,
                    onCheckedChange = {
                        unitToggleState = it
                        choiceState = if (it) "Imperial (F)" else "Metric (C)"
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clip(shape = RectangleShape)
                        .padding(5.dp)
                        .background(Color.Magenta.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (unitToggleState) "Fahrenheit ºF" else "Celsius ºC",
                        style = MaterialTheme.typography.button,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Save button
                Button(
                    onClick = {
                        // Delete all previous units and insert the new one
                        settingsViewModel.deleteAllUnits()
                        settingsViewModel.insertUnit(Unit(unit = choiceState))
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.4f),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1565C0) // A nice blue color
                    )
                ) {
                    Text(
                        text = "Save",
                        modifier = Modifier.padding(4.dp),
                        color = Color.White,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}
