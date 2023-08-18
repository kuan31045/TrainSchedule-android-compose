package com.kappstudio.trainschedule.domain.model

data class StopSchedule(
    val arrivalTime: String,
    val departureTime: String,
    val station: Station,
)