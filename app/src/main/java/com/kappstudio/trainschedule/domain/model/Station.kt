package com.kappstudio.trainschedule.domain.model

data class Station(
    val id: String = "",
    val name: Name = Name(),
    val county: Name = Name()
)