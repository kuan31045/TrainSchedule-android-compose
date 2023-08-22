package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kappstudio.trainschedule.domain.model.Name

data class TrainLiveBoardResponse(
    @SerializedName("TrainLiveBoards") val trainLiveBoards: List<TrainLiveBoardDto>?,
    @SerializedName("UpdateTime") val updateTime: String
)

data class TrainLiveBoardDto(
    @SerializedName("DelayTime") val delayTime: Int,
    @SerializedName("StationID") val stationId: String,
    @SerializedName("StationName") val stationName: Name,
    @SerializedName("TrainNo") val trainNo: String,
    @SerializedName("TrainStationStatus") val trainStationStatus: Int,
    @SerializedName("TrainTypeCode") val trainTypeCode: String,
    @SerializedName("TrainTypeID") val trainTypeId: String,
    @SerializedName("TrainTypeName") val trainTypeName: Name,
    @SerializedName("UpdateTime") val updateTime: String
)