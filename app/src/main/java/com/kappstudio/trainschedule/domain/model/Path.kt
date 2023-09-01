package com.kappstudio.trainschedule.domain.model

data class Path(
    val departureStation: Station = Station(),
    val arrivalStation: Station = Station(),
) {
    fun getTitle() =
        "${departureStation.name.localize()} " + "➝ ${arrivalStation.name.localize()}"
}