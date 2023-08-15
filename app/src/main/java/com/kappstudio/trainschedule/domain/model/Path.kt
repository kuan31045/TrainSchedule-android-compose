package com.kappstudio.trainschedule.domain.model

data class Path(
    val departureStation: Station = Station(),
    val arrivalStation: Station = Station()
)