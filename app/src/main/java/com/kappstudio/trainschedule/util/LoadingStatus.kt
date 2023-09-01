package com.kappstudio.trainschedule.util

import androidx.annotation.StringRes

sealed interface LoadingStatus {
    object Done : LoadingStatus
    data class Error(@StringRes val errorStringRes: Int) : LoadingStatus
    object Loading : LoadingStatus
}