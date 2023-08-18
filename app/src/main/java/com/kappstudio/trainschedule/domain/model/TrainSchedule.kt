package com.kappstudio.trainschedule.domain.model

data class TrainSchedule(
    val train: Train,
    val price: Int,
    val stops: List<StopSchedule> = emptyList(),
)