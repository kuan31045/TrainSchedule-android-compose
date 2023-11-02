package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kappstudio.trainschedule.domain.model.Name

data class StationLiveBoardResponse(
    @SerializedName("StationLiveBoards") val stationLiveBoards: List<StationLiveBoardDto>,
    @SerializedName("UpdateTime") val updateTime: String,
)

data class StationLiveBoardDto(
    @SerializedName("DelayTime") val delayTime: Int,
    @SerializedName("Direction") val direction: Int,
    @SerializedName("EndingStationID") val endingStationId: String,
    @SerializedName("EndingStationName") val endingStationName: Name,
    @SerializedName("Platform") val platform: String,
    @SerializedName("RunningStatus") val runningStatus: Int,
    @SerializedName("ScheduleArrivalTime") val scheduleArrivalTime: String,
    @SerializedName("ScheduleDepartureTime") val scheduleDepartureTime: String,
    @SerializedName("StationID") val stationId: String,
    @SerializedName("StationName") val stationName: Name,
    @SerializedName("TrainNo") val trainNo: String,
    @SerializedName("TrainTypeCode") val trainTypeCode: String,
    @SerializedName("TrainTypeID") val trainTypeId: String,
    @SerializedName("TrainTypeName") val trainTypeName: Name,
    @SerializedName("TripLine") val tripLine: Int,
    @SerializedName("UpdateTime") val updateTime: String,
)