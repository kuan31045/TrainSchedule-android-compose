package com.kappstudio.trainschedule.domain.model

import com.kappstudio.trainschedule.util.localize

data class Path(
    val departureStation: Station = Station(),
    val arrivalStation: Station = Station(),
) {
    fun getTitle() =
        "${departureStation.name.localize()} " + "➝ ${arrivalStation.name.localize()}"
}