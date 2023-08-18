package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.local.entity.PathEntity
import com.kappstudio.trainschedule.data.local.entity.StationEntity
import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.data.remote.dto.StopTimeDto
import com.kappstudio.trainschedule.data.remote.dto.TrainInfoDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.StopSchedule
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.TrainSchedule
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

fun StopTimeDto.toStop(): StopSchedule {
    return StopSchedule(
        arrivalTime = arrivalTime,
        departureTime = departureTime,
        station = Station(id = stationId, name = stationName)
    )
}

fun TrainInfoDto.toTrain(): Train {
    return Train(
        number = trainNo,
        name = trainTypeName,
        typeCode = trainTypeCode.toInt()
    )
}

fun TrainTimetableDto.toTrainSchedule(price: Int): TrainSchedule {
    return TrainSchedule(
        train = trainInfoDto.toTrain(),
        price = price,
        stops = stopTimes.map { it.toStop() }
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