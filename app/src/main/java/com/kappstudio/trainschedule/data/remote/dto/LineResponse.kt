package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LineResponse(
    @SerializedName("StationOfLines") val lines: List<LineDto>,
    @SerializedName("UpdateTime") val updateTime: String
)

data class LineDto(
    @SerializedName("LineID") val lineId: String,
    @SerializedName("Stations") val stations: List<StationDto>
)