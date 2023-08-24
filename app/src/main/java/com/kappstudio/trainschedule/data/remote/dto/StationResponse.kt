package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StationResponse(
    @SerializedName("UpdateTime") val  updateTime: String,
    @SerializedName("Stations") val stations: List<StationDto>,
)

