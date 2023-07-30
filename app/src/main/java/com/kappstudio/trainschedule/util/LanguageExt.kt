package com.kappstudio.trainschedule.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import com.kappstudio.trainschedule.domain.model.Name
import java.util.Locale.CHINESE
import java.util.Locale.ENGLISH

fun Name.localize(): String {
    return when (Locale.current.language) {
        CHINESE.language -> this.zh
        else -> this.en
    }
}

@Composable
fun TextStyle.localize(): TextStyle {
    return when (Locale.current.language) {
        CHINESE.language -> this
        else -> this.copy(fontSize = this.fontSize * 0.8)
    }
}
