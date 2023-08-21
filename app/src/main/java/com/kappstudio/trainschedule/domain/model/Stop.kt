package com.kappstudio.trainschedule.domain.model

data class Stop(
    val arrivalTime: String,
    val departureTime: String,
    val station: Station,
)