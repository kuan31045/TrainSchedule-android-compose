package com.kappstudio.trainschedule.domain.model

import com.google.gson.annotations.SerializedName

data class Name(
    @SerializedName("En") val en: String,
    @SerializedName("Zh_tw") val zh: String
)