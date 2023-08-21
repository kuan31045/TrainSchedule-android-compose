package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.calDurationMinutes
import com.kappstudio.trainschedule.util.timeFormatter
import java.time.Duration
import java.time.LocalTime

data class Trip(
    val path: Path = Path(),
    val transfers: List<Station> = emptyList(),
    val departureTime: String = "00:00",
    val arrivalTime: String = "00:00",
    val trainSchedules: List<TrainSchedule> = emptyList(),
) {
    val transferCount: Int = transfers.size
    val durationMinutes: Int = calDurationMinutes(departureTime, arrivalTime)
    val totalPrice: Int = trainSchedules.sumOf { it.price }
}