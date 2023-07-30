package com.kappstudio.trainschedule.domain.repository

import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Path
import kotlinx.coroutines.flow.Flow

interface TrainRepository {
    suspend fun getAccessToken(): String

    suspend fun fetchStations(): Result<List<Station>>

    val currentPath: Flow<Path>

    suspend fun savePath(path: Path)
 }
