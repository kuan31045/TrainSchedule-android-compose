package com.kappstudio.trainschedule.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
val dateWeekFormatter = DateTimeFormatter.ofPattern("MM/dd EEE")
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
val detailFormat = DateTimeFormatter.ofPattern("MM/dd EEE HH:mm")

fun LocalDateTime.toFormatterTime(): LocalTime =
    LocalTime.parse(this.format(timeFormatter))

/**
 * @param string HH:mm
 * */
fun String.addDate(date: LocalDate): LocalDateTime =
    LocalTime.parse(this).atDate(date)

fun getNowDateTime(): LocalDateTime =
    LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter))

fun LocalDateTime.toSec(): Long {
    val zoneId = ZoneId.of("Asia/Taipei")
    val zonedDateTime = this.atZone(zoneId)
    return zonedDateTime.toEpochSecond() * 1000
}