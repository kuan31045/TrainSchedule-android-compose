package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.calDurationMinutes

data class TrainSchedule(
    val train: Train,
    val price: Int,
    val stops: List<Stop>,
) {
    val departureTime = stops.first().departureTime
    val arrivalTime = stops.last().arrivalTime
    val durationMinutes: Int = calDurationMinutes(departureTime, arrivalTime)
}