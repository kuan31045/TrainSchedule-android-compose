package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.getNowDateTime
import java.time.LocalDateTime

data class Stop(
    val arrivalTime: LocalDateTime =  getNowDateTime(),
    val departureTime: LocalDateTime =  getNowDateTime(),
    val station: Station,
)