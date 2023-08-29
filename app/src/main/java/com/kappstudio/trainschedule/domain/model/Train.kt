package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.TrainFlag
import com.kappstudio.trainschedule.util.TrainStatus
import com.kappstudio.trainschedule.util.TrainType

data class Train(
    val number: String,
    val fullName: Name = Name(),
    val type: TrainType,
    val startStation: Station = Station(),
    val endStation: Station = Station(),
    val delay: Int? = null,
    val headSign: String = "",
    val note: String = "",
    val overNightStationId: String = "",
    val flags: List<TrainFlag> = emptyList(),
){
    val status: TrainStatus = TrainStatus.getTrainStatus(this)
    val isOverNight: Boolean = overNightStationId != ""
}