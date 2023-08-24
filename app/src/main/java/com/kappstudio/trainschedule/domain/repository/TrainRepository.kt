package com.kappstudio.trainschedule.domain.repository

import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TrainRepository {

    val currentPath: Flow<Path>

    suspend fun fetchAccessToken(): String

    suspend fun fetchStationsAndLines(): Result<List<Station>>

    suspend fun saveCurrentPath(path: Path)

    suspend fun fetchTrips(date: String): Result<List<Trip>>

    suspend fun fetchTransferTrips(date: String): Result<List<Trip>>

    suspend fun insertPath(path: Path)

    suspend fun deletePath(path: Path)

    fun getAllPathsStream(): Flow<List<Path>>

    suspend fun isCurrentPathFavorite(): Boolean

    suspend fun fetchTrainDelayTime(trainNumber: String): Int?

    suspend fun fetchTrainSchedule(trainNumber: String): Result<TrainSchedule>
}