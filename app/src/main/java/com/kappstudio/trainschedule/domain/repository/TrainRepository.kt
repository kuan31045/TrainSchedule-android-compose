package com.kappstudio.trainschedule.domain.repository

import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.local.entity.PathEntity
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TrainRepository {
    suspend fun getAccessToken(): String

    suspend fun fetchStations(): Result<List<Station>>

    suspend fun searchTrips(date: String): Result<List<Trip>>

    suspend fun searchTransferTrips(date: String): Result<List<Trip>>

    val currentPath: Flow<Path>

    suspend fun saveCurrentPath(path: Path)

    suspend fun insertPath(path: PathEntity)

    suspend fun deletePath(path: PathEntity)

    fun getAllPathsStream(): Flow<List<PathEntity>>

    suspend fun isCurrentPathFavorite(): Boolean
}