package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.timeFormatter
import java.time.Duration
import java.time.LocalTime

data class Trip(
    val trains: List<Train> = emptyList(),
    val transfers: List<Station> = emptyList(),
    val departureTime: String = "00:00",
    val arrivalTime: String = "00:00",
    val prices: List<Int> = emptyList(),
) {
    val transferCount: Int = trains.size - 1
    val durationMinutes: Int = calDurationMinutes()
    val totalPrice: Int = prices.sum()

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