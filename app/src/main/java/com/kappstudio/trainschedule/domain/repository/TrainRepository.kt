package com.kappstudio.trainschedule.domain.repository

import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.remote.dto.ODFareDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto
import com.kappstudio.trainschedule.domain.model.Line
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.StationLiveBoard
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import kotlinx.coroutines.flow.Flow
import org.jsoup.nodes.Document
import java.time.LocalDateTime

interface TrainRepository {

    val currentPath: Flow<Path>

    val selectedDateTime: Flow<LocalDateTime>

    suspend fun fetchAccessToken(): String

    suspend fun fetchStationsAndLines(): Result<Boolean>

    suspend fun saveCurrentPath(path: Path)

    suspend fun saveSelectedDateTime(dateTime: LocalDateTime)

    suspend fun fetchTimetables(path: Path): Result<List<TrainTimetableDto>>

    suspend fun scrapeTimetablesDocFromTwRailwayWeb(url: String): Result<Document>

    suspend fun fetchFares(path: Path): List<ODFareDto>?

    suspend fun insertPath(path: Path)

    suspend fun deletePath(path: Path)

    fun getAllPathsStream(): Flow<List<Path>>

    suspend fun getAllStations(): List<Station>

    fun getAllStationsStream(): Flow<List<Station>>

    fun getAllLinesStream(): Flow<List<Line>>

    suspend fun isCurrentPathFavorite(): Boolean

    suspend fun fetchTrainDelay(trainNumber: String): Int?

    suspend fun fetchTrainSchedule(trainNumber: String): Result<TrainSchedule>

    suspend fun getStationsOfLine(id: String): List<Station>

    suspend fun fetchStationLiveBoardOfTrain(trainNumber: String): List<StationLiveBoard>
}