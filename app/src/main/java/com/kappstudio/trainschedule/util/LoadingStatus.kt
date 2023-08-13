package com.kappstudio.trainschedule.util

sealed interface LoadingStatus {
    object Done : LoadingStatus
    data class Error(val error: String) : LoadingStatus
    object Loading : LoadingStatus
}