package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.TrainStatus

data class Train(
    val number: String,
    val name: Name = Name(),
    val typeCode: Int = 0,
    val startStation: Station = Station(),
    val endStation: Station = Station(),
    val stops: List<Stop> = emptyList(),
    val delayTime: Int? = null,
) {
    val status: TrainStatus = TrainStatus.getTrainStatus(this)
}