package com.example.weatherforecast.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.WeatherObject
import com.example.weatherforecast.navigation.WeatherScreens
import com.example.weatherforecast.screens.settings.SettingsViewModel
import com.example.weatherforecast.utils.formatDateTime
import com.example.weatherforecast.utils.formatDecimals
import com.example.weatherforecast.widgets.WeatherAppBar
import com.plcoding.weatherapp.domain.weather.WeatherType
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    city: String?
) {
    val curCity: String = if (city.isNullOrBlank()) "Cairo" else city
    val unitFromDb = settingsViewModel.unitList.collectAsState().value
    var isImperial by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = unitFromDb) {
        if (unitFromDb.isNotEmpty()) {
            val dbUnit = unitFromDb[0].unit.split(" ")[0].lowercase()
            isImperial = dbUnit == "imperial"
            mainViewModel.getWeatherData(city = curCity, units = dbUnit)
        } else {
            mainViewModel.getWeatherData(city = curCity, units = "imperial")
        }
    }

    val weatherData by mainViewModel.weatherData.collectAsState()

    if (weatherData.loading == true) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (weatherData.data!= null) {
        MainScaffold(weather = weatherData.data!!, navController = navController, isImperial = isImperial)
    } else if (weatherData.e!= null) {
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
            },
            elevation = 5.dp
        )
    }) { paddingValues ->
        MainContentNew(
            data = weather,
            isImperial = isImperial,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun MainContentNew(data: Weather, isImperial: Boolean, modifier: Modifier = Modifier) {
    val currentData = data.list.firstOrNull() ?: return
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1B3B5A))
    ) {
        WeatherCardNew(
            currentData = currentData,
            isImperial = isImperial,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        WeatherForecastNew(data = data, isImperial = isImperial)
    }
}

@Composable
fun WeatherCardNew(
    currentData: WeatherObject,
    isImperial: Boolean,
    modifier: Modifier = Modifier
) {
    val weatherType = WeatherType.fromOwm(currentData)
    val tempUnit = if (isImperial) "°F" else "°C"
    val windUnit = if (isImperial) "mph" else "km/h"

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today ${formatDateTime(currentData.dt, "EEE, MMM d")}",
                modifier = Modifier.align(Alignment.End),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            val iconRes = weatherType.iconRes
            val desc = weatherType.weatherDesc
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = desc,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${formatDecimals(currentData.temp.day)}$tempUnit",
                fontSize = 50.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = desc,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDataDisplay(
                    value = currentData.pressure,
                    unit = "hpa",
                    icon = Icons.Default.Compress
                )
                WeatherDataDisplay(
                    value = currentData.humidity,
                    unit = "%",
                    icon = Icons.Default.WaterDrop
                )
                WeatherDataDisplay(
                    value = currentData.speed.roundToInt(),
                    unit = windUnit,
                    icon = Icons.Default.Air
                )
            }
        }
    }
}

@Composable
fun WeatherForecastNew(data: Weather, isImperial: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Today",
            fontSize = 20.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(items = data.list) { weatherItem: WeatherObject ->
                HourlyWeatherDisplay(
                    weatherItem = weatherItem,
                    isImperial = isImperial,
                    modifier = Modifier
                        .height(110.dp)
                        .padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
fun HourlyWeatherDisplay(
    weatherItem: WeatherObject,
    isImperial: Boolean,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White
) {
    val weatherType = WeatherType.fromOwm(weatherItem)
    val tempUnit = if (isImperial) "°F" else "°C"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDateTime(weatherItem.dt, "hh:mm aa"),
            color = Color.LightGray
        )
        Image(
            painter = painterResource(id = weatherType.iconRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "${formatDecimals(weatherItem.temp.day)}$tempUnit",
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherDataDisplay(
    value: Int,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(color = Color.White),
    iconTint: Color = Color.White
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$value$unit",
            style = textStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // Provide fake data for preview
    val fakeCity = com.example.weatherforecast.model.City(
        coord = com.example.weatherforecast.model.Coord(30.0, 31.0),
        country = "EG",
        id = 1,
        name = "Cairo",
        population = 20000000,
        timezone = 7200
    )
    val fakeWeatherObject = WeatherObject(
        clouds = 10,
        deg = 90,
        dt = 1625074800,
        feels_like = com.example.weatherforecast.model.FeelsLike(30.0, 29.0, 28.0, 27.0),
        gust = 5.0,
        humidity = 60,
        pop = 0.1,
        pressure = 1012,
        rain = 0.0,
        speed = 10.0,
        sunrise = 1625038800,
        sunset = 1625091600,
        temp = com.example.weatherforecast.model.Temp(30.0, 32.0, 28.0, 27.0, 31.0, 29.0),
        weather = listOf(
            com.example.weatherforecast.model.WeatherX(
                description = "clear sky",
                icon = "01d",
                id = 800,
                main = "Clear"
            )
        )
    )
    val fakeWeather = com.example.weatherforecast.model.Weather(
        city = fakeCity,
        cnt = 1,
        cod = "200",
        list = listOf(fakeWeatherObject),
        message = 0.0
    )
    // Use a real NavController for preview
    val navController = androidx.navigation.compose.rememberNavController()
    MainScaffold(weather = fakeWeather, navController = navController, isImperial = false)
}
