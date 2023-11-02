package com.kappstudio.trainschedule.domain.model

import com.google.gson.annotations.SerializedName

data class TrainLiveBoard(
    val trainNumber: String,
    val stationId: String,
    val delay: Long,
    val updateTime: String,
)