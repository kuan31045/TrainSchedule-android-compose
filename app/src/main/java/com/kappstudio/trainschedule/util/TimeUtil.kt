package com.kappstudio.trainschedule.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
val dateWeekFormatter = DateTimeFormatter.ofPattern("MM/dd EEE")
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
val detailFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

fun LocalDateTime.toFormatterTime(): LocalTime =
    LocalTime.parse(this.format(timeFormatter))

/**
 * @param string HH:mm
 * */
fun String.addDate(date: LocalDate): LocalDateTime =
    LocalTime.parse(this).atDate(date)

fun getNowDateTime(): LocalDateTime =
    LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter))