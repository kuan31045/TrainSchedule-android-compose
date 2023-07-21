package com.kappstudio.trainschedule.domain.repository

import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.data.Result

interface TrainRepository {
    suspend fun getStations(): Result<List<Station>>
    suspend fun getAccessToken(): String
}
