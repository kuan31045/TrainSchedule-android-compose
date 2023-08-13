package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.util.countyMap
import com.kappstudio.trainschedule.util.detailFormatter
import com.kappstudio.trainschedule.util.timeFormatter
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun TrainTimetableDto.toTrip(): Trip {
    return Trip(
        trains = listOf(Train(number = trainInfoDto.trainNo, name = trainInfoDto.trainTypeName)),
        departureTime = stopTimes.first().departureTime,
        arrivalTime = stopTimes.last().arrivalTime,
    )
}