package com.kappstudio.trainschedule.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
val dateWeekFormatter = DateTimeFormatter.ofPattern("MM/dd EEE")
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val detailFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

fun String.toDateWeekFormatter(): String =
    LocalDate.parse(this).format(dateWeekFormatter).toString()

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

fun checkIsRunning(
    date: String,
    startTime: String,
    endTime: String,
    isOverNight: Boolean,
): Boolean {
    val date1 = LocalDate.parse(date)
    val time1 = LocalTime.parse(startTime)

    val date2 = if (isOverNight) date1.plusDays(1) else date1
    val time2 = LocalTime.parse(endTime)

    val dateTime1 = LocalDateTime.of(date1, time1).minusMinutes(1)
    val dateTime2 = LocalDateTime.of(date2, time2).plusMinutes(1)

    val currentDateTime = LocalDateTime.now()

    return (currentDateTime.isAfter(dateTime1) && currentDateTime.isBefore(dateTime2))
}