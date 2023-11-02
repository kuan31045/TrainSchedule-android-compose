package com.kappstudio.trainschedule.domain.model

data class StationLiveBoard(
    val trainNumber: String,
    val stationId: String,
    val delay: Long,
    val runningStatus: Int,
    val updateTime: String,
)