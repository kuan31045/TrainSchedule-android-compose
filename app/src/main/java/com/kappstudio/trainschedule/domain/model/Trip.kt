package com.kappstudio.trainschedule.domain.model

import java.time.Duration
import java.time.LocalDateTime

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
        val departureH = departureTime.split(":")[0].toInt()
        val departureM = departureTime.split(":")[1].toInt()

        val arrivalH = arrivalTime.split(":")[0].toInt()
        val arrivalM = arrivalTime.split(":")[1].toInt()

        return if (departureTime < arrivalTime) {
            (arrivalH * 60 + arrivalM) - (departureH * 60 + departureM)
        } else {
            ((arrivalH + 24) * 60 + arrivalM) - (departureH * 60 + departureM)
        }
    }
}