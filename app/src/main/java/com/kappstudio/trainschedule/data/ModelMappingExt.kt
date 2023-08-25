package com.kappstudio.trainschedule.data

import com.kappstudio.trainschedule.data.local.entity.LineEntity
import com.kappstudio.trainschedule.data.local.entity.PathEntity
import com.kappstudio.trainschedule.data.local.entity.StationEntity
import com.kappstudio.trainschedule.data.remote.dto.LineDto
import com.kappstudio.trainschedule.data.remote.dto.StationDto
import com.kappstudio.trainschedule.data.remote.dto.StopTimeDto
import com.kappstudio.trainschedule.data.remote.dto.TrainInfoDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.model.Stop
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.util.TrainFlag
import com.kappstudio.trainschedule.util.addDate
import com.kappstudio.trainschedule.util.countyMap
import java.time.LocalDate

fun StationDto.toStation(): Station {
    val countyZh = stationAddress?.filter { !it.isDigit() }?.take(2)?.ifEmpty { "" } ?: ""
    return Station(
        id = stationId,
        name = stationName,
        county = Name(
            en = countyMap[countyZh] ?: "",
            zh = countyZh
        )
    )
}

fun StopTimeDto.toStop(date: LocalDate): Stop {
    val isDifferentDay = arrivalTime > departureTime
    val deductedDay: Long = if (isDifferentDay) 1 else 0
    return Stop(
        arrivalTime = arrivalTime.addDate(date).minusDays(deductedDay),
        departureTime = departureTime.addDate(date),
        station = Station(id = stationId, name = stationName)
    )
}

fun TrainInfoDto.toTrain(): Train {
    return Train(
        number = trainNo,
        fullName = trainTypeName,
        typeCode = trainTypeCode.toInt(),
        startStation = Station(id = startingStationId, name = startingStationName),
        endStation = Station(id = endingStationId, name = endingStationName),
        headSign = headSign,
        note = note,
        overNightStationId = overNightStationId ?: "",
        flags = TrainFlag.getFlagList(
            daily = dailyFlag,
            bike = bikeFlag,
            wheel = wheelChairFlag,
            breastfeeding = breastFeedFlag
        )
    )
}

fun TrainTimetableDto.toTrainSchedule(price: Int = 0, date: LocalDate): TrainSchedule {

    val overNightIndex = trainInfoDto.overNightStationId?.let { id ->
        stopTimes.indexOfFirst { it.stationId == id }
    } ?: Int.MAX_VALUE

    return TrainSchedule(
        train = trainInfoDto.toTrain(),
        price = price,
        stops = stopTimes.mapIndexed { index, stop ->
            val nextDay: Long = if (index >= overNightIndex && trainInfoDto.isOverNight) 1 else 0
            stop.toStop(date.plusDays(nextDay))
        }
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

fun StationDto.toStationEntity(): StationEntity {
    return this.toStation().toStationEntity()
}

fun StationEntity.toStation(): Station {
    return Station(
        id = id,
        name = name,
        county = county
    )
}

fun LineDto.toLineEntity(): LineEntity {
    return LineEntity(
        id = lineId,
        stations = stations.map { it.toStationEntity() }
    )
}