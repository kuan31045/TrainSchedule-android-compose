package com.kappstudio.trainschedule.domain.model

data class Train(
    val number: String,
    val name: Name = Name(),
    val trainType: Int = 0,
)
