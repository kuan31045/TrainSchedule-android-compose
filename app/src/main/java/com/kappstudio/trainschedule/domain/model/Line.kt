package com.kappstudio.trainschedule.domain.model

data class Line(
    val id: String,
    val stations: List<Station>,
)