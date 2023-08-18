package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.timeFormatter
import java.time.Duration
import java.time.LocalTime

data class Trip(
    val transfers: List<Station> = emptyList(),
    val departureTime: String = "00:00",
    val arrivalTime: String = "00:00",
    val trainSchedules: List<TrainSchedule> = emptyList(),
) {
    val transferCount: Int = transfers.size
    val durationMinutes: Int = calDurationMinutes()
    val totalPrice: Int = trainSchedules.sumOf { it.price }

    private fun calDurationMinutes(): Int {
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
}