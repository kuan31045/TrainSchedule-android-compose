package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.util.countyMap

fun StationDto.toStation(): Station {
    val countyZh = stationAddress.filter { !it.isDigit() }.take(2).ifEmpty { "" }
    return Station(
        id = stationId,
        name = stationName,
        county = Name(
            en = countyMap[countyZh] ?: "",
            zh = countyZh
        )
    )
}