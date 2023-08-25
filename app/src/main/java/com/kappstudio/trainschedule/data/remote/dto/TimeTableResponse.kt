package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kappstudio.trainschedule.domain.model.Name

data class TimeTableResponse(
    @SerializedName("TrainTimetables") val trainTimetables: List<TrainTimetableDto>,
    @SerializedName("UpdateTime") val updateTime: String,
)

data class TrainTimetableDto(
    @SerializedName("StopTimes") val stopTimes: List<StopTimeDto>,
    @SerializedName("TrainInfo") val trainInfoDto: TrainInfoDto,
)

data class StopTimeDto(
    @SerializedName("ArrivalTime") val arrivalTime: String,
    @SerializedName("DepartureTime") val departureTime: String,
    @SerializedName("StationID") val stationId: String,
    @SerializedName("StationName") val stationName: Name,
    @SerializedName("StopSequence") val stopSequence: Int,
)