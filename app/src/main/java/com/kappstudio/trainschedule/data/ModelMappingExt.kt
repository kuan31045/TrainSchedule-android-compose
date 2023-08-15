package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.local.entity.PathEntity
import com.kappstudio.trainschedule.data.local.entity.StationEntity
import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
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

fun Path.toPathEntity(): PathEntity {
    return PathEntity(
        id = "${departureStation.id}-${arrivalStation.id}",
        departureStation = departureStation.toStationEntity(),
        arrivalStation = arrivalStation.toStationEntity()
    )
}

fun PathEntity.toPath(): Path {
    return Path(
        departureStation = departureStation.toStation(),
        arrivalStation = arrivalStation.toStation()
    )
}

fun Station.toStationEntity(): StationEntity {
    return StationEntity(
        id = id,
        name = name,
        county = county
    )
}

fun StationEntity.toStation(): Station {
    return Station(
        id = id,
        name = name,
        county = county
    )
}