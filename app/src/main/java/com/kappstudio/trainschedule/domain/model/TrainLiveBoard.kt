package com.kappstudio.trainschedule.domain.model

data class TrainLiveBoard(
    val trainNumber: String,
    val stationId: String,
    val delay: Long,
    val updateTime: String,
)