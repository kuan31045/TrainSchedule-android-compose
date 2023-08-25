package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.getNowDateTime
import java.time.Duration
import java.time.LocalDateTime

data class Trip(
    val path: Path = Path(),
    val transfers: List<Station> = emptyList(),
    val startTime: LocalDateTime =  getNowDateTime(),
    val endTime: LocalDateTime =  getNowDateTime(),
    val trainSchedules: List<TrainSchedule> = emptyList(),
) {
    val transferCount: Int = transfers.size
    val durationMinutes: Long = Duration.between(startTime, endTime ).toMinutes()
    val totalPrice: Int = trainSchedules.sumOf { it.price }
}