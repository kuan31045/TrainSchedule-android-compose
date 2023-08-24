package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.TrainFlag
import com.kappstudio.trainschedule.util.TrainStatus

data class Train(
    val number: String,
    val fullName: Name = Name(),
    val typeCode: Int = 0,
    val startStation: Station = Station(),
    val endStation: Station = Station(),
    val delayTime: Int? = null,
    val headSign: String = "",
    val note: String = "",
    val overNightStationId: String = "",
    val flags: List<TrainFlag> = emptyList(),
    val routeId: String?="",
    val tripLine: Int?=-1,
) {
    val status: TrainStatus = TrainStatus.getTrainStatus(this)
    val isOverNight: Boolean = overNightStationId != ""
}