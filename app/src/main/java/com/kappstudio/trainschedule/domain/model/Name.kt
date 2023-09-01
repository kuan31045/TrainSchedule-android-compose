package com.kappstudio.trainschedule.domain.model

import androidx.compose.ui.text.intl.Locale
import com.google.gson.annotations.SerializedName

data class Name(
    @SerializedName("En") val en: String = "",
    @SerializedName("Zh_tw") val zh: String = ""
){
    fun localize(): String {
        return when (Locale.current.language) {
            java.util.Locale.CHINESE.language -> this.zh
            else -> this.en
        }
    }
}