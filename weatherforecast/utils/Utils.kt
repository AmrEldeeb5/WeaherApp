package com.example.weatherforecast.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formats a Unix timestamp into a string representing the date (e.g., "EEE, MMM d").
 * @param timestamp The Unix timestamp in seconds.
 * @return A formatted date string.
 */
fun formatDate(timestamp: Int): String {
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val date = Date(timestamp.toLong() * 1000)
    return sdf.format(date)
}

/**
 * Formats a Unix timestamp into a string with a custom pattern.
 * This is a flexible function that can be used for time, date, or both.
 * @param timestamp The Unix timestamp in seconds.
 * @param format The pattern to format the date/time with (e.g., "hh:mm:aa", "HH:mm").
 * @return A formatted string based on the provided pattern.
 */
fun formatDateTime(timestamp: Int, format: String): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = Date(timestamp.toLong() * 1000)
    return sdf.format(date)
}


/**
 * Formats a Double value into a string with no decimal places.
 * @param item The Double value to format.
 * @return A formatted string (e.g., "32").
 */
fun formatDecimals(item: Double): String {
    return "%.0f".format(item)
}
