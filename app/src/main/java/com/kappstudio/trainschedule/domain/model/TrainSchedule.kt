package com.kappstudio.trainschedule.domain.model

import androidx.annotation.StringRes
import com.kappstudio.trainschedule.util.calDurationMinutes
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.util.timeFormatter
import java.time.LocalTime

data class TrainSchedule(
    val train: Train,
    val price: Int,
    val stops: List<Stop>,
) {
    val departureTime = stops.first().departureTime
    val arrivalTime = stops.last().arrivalTime
    val durationMinutes: Int = calDurationMinutes(departureTime, arrivalTime)
}