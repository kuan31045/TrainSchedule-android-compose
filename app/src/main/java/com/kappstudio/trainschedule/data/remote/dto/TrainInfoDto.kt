package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kappstudio.trainschedule.domain.model.Name

data class TrainInfoDto(
    @SerializedName("TrainNo") val trainNo: String,
    @SerializedName("TrainTypeCode") val trainTypeCode: String,
    @SerializedName("TrainTypeID") val trainTypeId: String,
    @SerializedName("TrainTypeName") val trainTypeName: Name,
    @SerializedName("StartingStationID") val startingStationId: String,
    @SerializedName("StartingStationName") val startingStationName: Name,
    @SerializedName("EndingStationID") val endingStationId: String,
    @SerializedName("EndingStationName") val endingStationName: Name,
    @SerializedName("OverNightStationID") val overNightStationId: String,
    @SerializedName("RouteID") val routeId: String,
    @SerializedName("Direction") val direction: Int,
    @SerializedName("BikeFlag") val bikeFlag: Int,
    @SerializedName("BreastFeedFlag") val breastFeedFlag: Int,
    @SerializedName("DailyFlag") val dailyFlag: Int,
    @SerializedName("ExtraTrainFlag") val extraTrainFlag: Int,
    @SerializedName("PackageServiceFlag") val packageServiceFlag: Int,
    @SerializedName("SuspendedFlag") val suspendedFlag: Int,
    @SerializedName("WheelChairFlag") val wheelChairFlag: Int
)