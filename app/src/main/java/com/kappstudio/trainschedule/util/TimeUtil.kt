package com.kappstudio.trainschedule.util

import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
val dateWeekFormatter = DateTimeFormatter.ofPattern("MM/dd EEE")
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val detailFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

fun calDurationMinutes(
    departureTime: String,
    arrivalTime: String,
): Int {
    val duration = Duration.between(
        LocalTime.parse(departureTime, timeFormatter),
        LocalTime.parse(arrivalTime, timeFormatter)
    ).toMinutes().toInt()

    return if (departureTime > arrivalTime) {
        duration + 1440
    } else {
        duration
    }
}