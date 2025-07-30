package com.example.weatherforecast.screens.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.data.DataOrException
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.model.WeatherItem
import com.example.weatherforecast.screens.settings.SettingsViewModel
import com.example.weatherforecast.utils.formatDateTime
import com.example.weatherforecast.utils.formatDecimals
import com.example.weatherforecast.widgets.WeatherAppBar
import com.example.weatherforecast.navigation.WeatherScreens
import com.example.weatherforecast.screens.Main.MainViewModel
import com.example.weatherforecast.screens.main.MainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    var unit by remember { mutableStateOf("imperial") }
    var isImperial by remember { mutableStateOf(false) }

    if (unitFromDb.isNotEmpty()) {
        unit = unitFromDb[0].unit.split(" ")[0].lowercase()
        isImperial = unit == "imperial"

        val weatherData = produceState<DataOrException<Weather, Boolean, Exception>>(
            initialValue = DataOrException(loading = true)
        ) {
            value = mainViewModel.getWeatherdata (city = curCity, units = unit)
        }.value

        if (weatherData.loading == true) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        } else if (weatherData.data != null) {
            MainScaffold(weather = weatherData.data!!, navController, isImperial = isImperial)
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1B3B5A)) // DarkBlue
    ) {
        WeatherCardNew(
            data = data,
            isImperial = isImperial,
            backgroundColor = Color(0xFF102840) // DeepBlue
        )
        Spacer(modifier = Modifier.height(16.dp))
        WeatherForecastNew(data = data, isImperial = isImperial)
    }
}


@Composable
fun WeatherCardNew(
    data: Weather,
    isImperial: Boolean,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val currentData = data.list[0]
    val weatherType = WeatherType.fromOwm(currentData)
    val tempUnit = if (isImperial) "°F" else "°C"
    val windUnit = if (isImperial) "mph" else "km/h"

    Card(
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today ${formatDateTime(currentData.dt)}",
                modifier = Modifier.align(Alignment.End),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = weatherType.iconRes),
                contentDescription = weatherType.weatherDesc,
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
                text = weatherType.weatherDesc,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherDataDisplay(
                    value = currentData.pressure.roundToInt(),
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
            items(items = data.list) { weatherItem: WeatherItem ->
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
    weatherItem: WeatherItem,
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
            text = formatDateTime(weatherItem.dt),
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

fun formatDate(timestamp: Int, format: String): String {
    val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
    val date = java.util.Date(timestamp.toLong() * 1000)
    return sdf.format(date)
}


sealed class WeatherType(
    val weatherDesc: String,
    @DrawableRes val iconRes: Int
) {
    object ClearSky : WeatherType("Clear Sky", R.drawable.ic_sunny)
    object MainlyClear : WeatherType("Mainly Clear", R.drawable.ic_cloudy)
    object PartlyCloudy : WeatherType("Partly Cloudy", R.drawable.ic_cloudy)
    object Overcast : WeatherType("Overcast", R.drawable.ic_cloudy)
    object Drizzle : WeatherType("Drizzle", R.drawable.ic_rainshower)
    object Rain : WeatherType("Rain", R.drawable.ic_rainy)
    object HeavyRain : WeatherType("Heavy Rain", R.drawable.ic_rainy)
    object RainShowers : WeatherType("Rain Showers", R.drawable.ic_rainshower)
    object Thunderstorm : WeatherType("Thunderstorm", R.drawable.ic_thunder)
    object Snow : WeatherType("Snow", R.drawable.ic_snowy)
    object HeavySnow : WeatherType("Heavy Snow", R.drawable.ic_heavysnow)
    object Fog : WeatherType("Fog", R.drawable.ic_very_cloudy)

    companion object {
        fun fromOwm(weatherItem: WeatherItem): WeatherType {
            return when (weatherItem.weather[0].id) {
                in 200..232 -> Thunderstorm
                in 300..321 -> Drizzle
                in 500..504 -> Rain
                511 -> Snow
                in 520..531 -> RainShowers
                in 600..601 -> Snow
                602 -> HeavySnow
                in 611..622 -> Snow
                in 701..781 -> Fog
                800 -> ClearSky
                801 -> MainlyClear
                802 -> PartlyCloudy
                803, 804 -> Overcast
                else -> ClearSky
            }
        }
    }
}