package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto

import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.Trip
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

fun TrainTimetableDto.toTrip(prices: List<Int>): Trip {
    return Trip(
        trains = listOf(
            Train(
                number = trainInfoDto.trainNo,
                name = trainInfoDto.trainTypeName,
                trainType = trainInfoDto.trainTypeCode.toIntOrNull() ?: 0
            )
        ),
        departureTime = stopTimes.first().departureTime,
        arrivalTime = stopTimes.last().arrivalTime,
        prices = prices
    )
}
