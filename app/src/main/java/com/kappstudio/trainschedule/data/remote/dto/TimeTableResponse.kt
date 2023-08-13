package com.kappstudio.trainschedule.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TimeTableResponse(
    @SerializedName("TrainTimetables") val trainTimetables: List<TrainTimetableDto>,
    @SerializedName("UpdateTime") val  updateTime: String,
)

data class TrainTimetableDto(
    @SerializedName("StopTimes") val stopTimes: List<StopTimeDto>,
    @SerializedName("TrainInfo") val trainInfoDto: TrainInfoDto
)