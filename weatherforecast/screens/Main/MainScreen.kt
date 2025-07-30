package com.example.weatherforecast.screens.main

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.weatherforecast.widgets.WeatherAppBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.WeatherObject
import com.example.weatherforecast.navigation.WeatherScreens
import com.example.weatherforecast.screens.settings.SettingsViewModel
import com.example.weatherforecast.utils.formatDate
import com.example.weatherforecast.utils.formatDecimals
import com.example.weatherforecast.widgets.*

@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    city: String?
) {
    val curCity: String = if (city.isNullOrBlank()) "Cairo" else city
    val unitFromDb = settingsViewModel.unitList.collectAsState().value
    var unit by remember { mutableStateOf("imperial") }
    var isImperial by remember { mutableStateOf(false) }

    LaunchedEffect(unitFromDb) {
        if (unitFromDb.isNotEmpty()) {
            unit = unitFromDb[0].unit.split(" ")[0].lowercase()
            isImperial = unit == "imperial"
            mainViewModel.getWeatherData(city = curCity, units = unit)
        } else {
            mainViewModel.getWeatherData(city = curCity, units = "imperial")
        }
    }

    val weatherData by mainViewModel.weatherData.collectAsState()

    if (weatherData.loading == true) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (weatherData.data != null) {
        MainScaffold(
            weather = weatherData.data!!,
            navController = navController,
            isImperial = isImperial
        )
    } else if (weatherData.e != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("An error occurred: ${weatherData.e!!.message}")
        }
    }
}

@Composable
fun MainScaffold(
    weather: Weather,
    navController: NavController,
    isImperial: Boolean
) {
    Scaffold(topBar = {
        WeatherAppBar(
            title = weather.city.name + ", " + weather.city.country,
            navController = navController,
            onAddActionClicked = {
                navController.navigate(WeatherScreens.SearchScreen.name)
            }
        )
    }) { padding ->
        MainContent(data = weather, isImperial = isImperial, modifier = Modifier.padding(padding))
    }
}

@Composable
fun MainContent(data: Weather, isImperial: Boolean, modifier: Modifier = Modifier) {
    val weatherItem = data.list.firstOrNull() ?: return
    val temp = weatherItem.temp
    val imageUrl = "https://openweathermap.org/img/wn/${weatherItem.weather.firstOrNull()?.icon ?: "01d"}.png"

    Column(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatDate(weatherItem.dt),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(6.dp)
        )

        Surface(
            modifier = Modifier
                .padding(4.dp)
                .size(200.dp),
            shape = CircleShape,
            color = Color(0xFFFFC400)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherStateImage(imageUrl = imageUrl)
                if (temp != null) {
                    Text(
                        text = formatDecimals(temp.day) + "º",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else {
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = weatherItem.weather.firstOrNull()?.main ?: "",
                    fontStyle = FontStyle.Italic
                )
            }
        }
        androidx.compose.material3.HorizontalDivider()
        Text(
            "This Week",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = Color(0xFFEEF1EF),
            shape = RoundedCornerShape(size = 14.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(2.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                items(items = data.list) { item: WeatherObject ->
                    val tempDay = item.temp?.day?.let { formatDecimals(it) + "º" } ?: "-"
                    Text(text = formatDate(item.dt) + ": " + tempDay)
                }
            }
        }
    }
}

@Composable
fun WeatherStateImage(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = "icon image",
        modifier = Modifier.size(80.dp)
    )
}
