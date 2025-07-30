package com.example.weatherforecast.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Formats a Double value to a String with exactly one decimal place.
 *
 * This function is crucial for displaying temperature. It explicitly uses
 * Locale.US to ensure the decimal separator is always a period ('.')
 * and not a comma (','), preventing UI inconsistencies and potential
 * parsing errors across different user devices.
 *
 * @param item The Double value to format.
 * @return A String formatted to one decimal place (e.g., "31.7").
 */
fun formatDecimals(item: Double): String {
    return String.format(Locale.US, "%.1f", item)
}

/**
 * Formats a Unix timestamp into a time string (e.g., "15:00").
 *
 * This function uses the modern and thread-safe java.time API. It converts
 * the timestamp to the user's local time zone before formatting.
 *
 * @param timestamp The Unix timestamp (in seconds).
 * @return A formatted time string in "HH:mm" format.
 */
fun formatDateTime(timestamp: Int): String {
    // Create a formatter for "HH:mm" format (e.g., 15:00)
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    // Convert the Unix timestamp (seconds) to an Instant
    val instant = Instant.ofEpochSecond(timestamp.toLong())

    // Convert the Instant to the user's local time zone
    val localTime = instant.atZone(ZoneId.systemDefault())

    return formatter.format(localTime)
}
