package com.plcoding.weatherapp.domain.weather

import androidx.annotation.DrawableRes
import com.example.weatherforecast.R
import com.example.weatherforecast.model.WeatherObject

sealed class WeatherType(
    val weatherDesc: String,
    @param:DrawableRes val iconRes: Int // Use @param: to avoid annotation warning
) {
    companion object {
        fun fromOwm(weatherItem: WeatherObject): WeatherType {
            val id = weatherItem.weather.firstOrNull()?.id ?: 800
            return when (id) {
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
}