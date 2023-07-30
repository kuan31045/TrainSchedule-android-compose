package com.kappstudio.trainschedule.util

sealed interface LoadApiStatus {
    object Done : LoadApiStatus
    data class Error(val error: String) : LoadApiStatus
    object Loading : LoadApiStatus
}