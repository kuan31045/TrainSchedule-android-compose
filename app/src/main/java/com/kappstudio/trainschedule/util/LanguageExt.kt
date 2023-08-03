package com.kappstudio.trainschedule.util

import androidx.compose.ui.text.intl.Locale
import com.kappstudio.trainschedule.domain.model.Name
import java.util.Locale.CHINESE

fun Name.localize(): String {
    return when (Locale.current.language) {
        CHINESE.language -> this.zh
        else -> this.en
    }
}