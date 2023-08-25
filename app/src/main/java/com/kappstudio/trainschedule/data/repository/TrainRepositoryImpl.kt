package com.kappstudio.trainschedule.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.kappstudio.trainschedule.data.remote.TrainApi
import com.kappstudio.trainschedule.data.toStation
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.local.TrainDatabase
import com.kappstudio.trainschedule.data.remote.dto.TokenDto
import com.kappstudio.trainschedule.data.remote.dto.TrainTimetableDto
import com.kappstudio.trainschedule.data.toLineEntity
import com.kappstudio.trainschedule.data.toPath
import com.kappstudio.trainschedule.data.toPathEntity
import com.kappstudio.trainschedule.data.toStationEntity
import com.kappstudio.trainschedule.data.toTrainSchedule
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.util.addDate
import com.kappstudio.trainschedule.util.getNowDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime

class TrainRepositoryImpl @Inject constructor(
    private val api: TrainApi,
    private val dataStore: DataStore<Preferences>,
    private val trainDb: TrainDatabase,
) : TrainRepository {

    private val localToken = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e("Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            TokenDto(
                accessToken = preferences[ACCESS_TOKEN] ?: "",
                expiresIn = preferences[TOKEN_EXPIRE_TIME] ?: 0
            )
        }

    override val currentPath: Flow<Path> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e("Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val json = preferences[CURRENT_PATH]
            val path = Gson().fromJson(json, Path::class.java)
            path ?: defaultPath
        }

    override val selectedDateTime: Flow<LocalDateTime> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e("Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val date = preferences[SELECTED_DATE_TIME]
            if (date != null) {
                LocalDateTime.parse(date)
            } else {
                getNowDateTime()
            }
        }

    override suspend fun fetchAccessToken(): String {
        if (localToken.first().expiresIn < System.currentTimeMillis()) {
            try {
                //Get token from Api
                val newToken = api.getAccessToken()
                //Save token into DataStore
                dataStore.edit { preferences ->
                    preferences[ACCESS_TOKEN] = BEARER + newToken.accessToken
                    preferences[TOKEN_EXPIRE_TIME] =
                        newToken.expiresIn * 1000 / 2 + System.currentTimeMillis()
                }
            } catch (e: Exception) {
                Timber.w("getToken exception = ${e.message}")
            }
        }
        return localToken.first().accessToken
    }

    override suspend fun fetchStationsAndLines(): Result<List<Station>> {
        return try {
            val stationResult = api.getStations(fetchAccessToken())
            trainDb.stationDao.upsertAll(stationResult.stations.map { it.toStationEntity() })

            val lineResult = api.getLines(fetchAccessToken())
            trainDb.lineDao.upsertAll(lineResult.lines.map { it.toLineEntity() })

            Result.Success(stationResult.stations.map { it.toStation() })
        } catch (e: Exception) {
            Timber.w("getStations exception = ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun saveCurrentPath(path: Path) {
        val json = Gson().toJson(path)
        dataStore.edit { preferences ->
            preferences[CURRENT_PATH] = json
        }
    }

    override suspend fun saveSelectedDateTime(dateTime: LocalDateTime) {
        dataStore.edit { preferences ->
            preferences[SELECTED_DATE_TIME] = dateTime.toString()
        }
    }

    override suspend fun fetchTrips(): Result<List<Trip>> {

        val date = selectedDateTime.first().toLocalDate()

        return try {
            val result = api.getTrainTimetable(
                token = fetchAccessToken(),
                departureStationId = currentPath.first().departureStation.id,
                arrivalStationId = currentPath.first().arrivalStation.id,
                date = date.toString()
            )
            delay(500)

            val fares = api.getODFare(
                token = fetchAccessToken(),
                departureStationId = currentPath.first().departureStation.id,
                arrivalStationId = currentPath.first().arrivalStation.id,
            ).odFares

            Result.Success(result.trainTimetables.map { timeTable ->

                val startTime = timeTable.stopTimes.first().departureTime.addDate(date)
                val endTime = timeTable.stopTimes.last().arrivalTime.addDate(date)

                Trip(
                    path = currentPath.first(),
                    startTime = startTime,
                    endTime = if (endTime >= startTime) endTime else endTime.plusDays(1),
                    trainSchedules = listOf(
                        timeTable.toTrainSchedule(
                            price = fares.firstOrNull { fare ->
                                timeTable.trainInfoDto.direction == fare.direction
                                        && timeTable.trainInfoDto.trainTypeCode.toInt() == fare.trainType
                            }?.fares?.first()?.price ?: -1,
                            date = selectedDateTime.first().toLocalDate()
                        )
                    )
                )
            })
        } catch (e: Exception) {
            Timber.w("getTrainTimetable exception = ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun fetchTransferTrips(): Result<List<Trip>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPath(path: Path) = trainDb.pathDao.insert(path.toPathEntity())

    override suspend fun deletePath(path: Path) = trainDb.pathDao.delete(path.toPathEntity())

    override fun getAllPathsStream(): Flow<List<Path>> {
        return trainDb.pathDao.getAllPaths().map { pathEntities ->
            withContext(Dispatchers.IO) {
                pathEntities.map { it.toPath() }
            }
        }
    }

    override suspend fun isCurrentPathFavorite(): Boolean {
        return withContext(Dispatchers.IO) {
            trainDb.pathDao.getPath(currentPath.first().toPathEntity().id) != null
        }
    }

    override suspend fun fetchTrainDelay(trainNumber: String): Int? {
        return try {
            val result = api.getTrainLiveBoard(fetchAccessToken(), trainNumber)
            result.trainLiveBoards?.first()?.delayTime
        } catch (e: Exception) {
            Timber.w("getTrainLiveBoard exception = ${e.message}")
            null
        }
    }

    override suspend fun fetchTrainSchedule(trainNumber: String): Result<TrainSchedule> {
        return try {
            val result = api.getGeneralTrainTimetable(
                token = fetchAccessToken(), trainNumber
            )
            if (result.trainTimetables.isNotEmpty()) {
                val previousDay: Long = checkPreviousDay(result.trainTimetables.first())
                Result.Success(
                    result.trainTimetables.first().toTrainSchedule(
                        date = selectedDateTime.first().toLocalDate().minusDays(previousDay)
                    )
                )
            } else {
                val todayResult = api.getTodayTrainTimetable(
                    token = fetchAccessToken()
                )
                val previousDay: Long = checkPreviousDay(todayResult.trainTimetables.first())
                Result.Success(todayResult.trainTimetables.first { it.trainInfoDto.trainNo == trainNumber }
                    .toTrainSchedule(
                        date = selectedDateTime.first().toLocalDate().minusDays(previousDay)
                    ))
            }
        } catch (e: Exception) {
            Timber.w("fetchTrain exception = ${e.message}")
            Result.Error(e)
        }
    }


    override suspend fun getStationsOfLine(id: String): List<Station> {
        return withContext(Dispatchers.IO) {
            trainDb.lineDao.get(id).stations.map { it.toStation() }
        }
    }

    /**
     * 如果是跨日列車且查詢站在換日站之後，表示換日在查詢站之前就發生，所以列車出發時間扣除一天
     */
    private suspend fun checkPreviousDay(timeTable: TrainTimetableDto): Long {
        val stops = timeTable.stopTimes
        val overNightStationId = timeTable.trainInfoDto.overNightStationId
        val overNightIndex = stops.indexOfFirst { it.stationId == overNightStationId }
        val queryIndex =
            stops.indexOfFirst { it.stationId == currentPath.first().departureStation.id }
        return if (overNightIndex != -1 && overNightIndex <= queryIndex) 1 else 0
    }

    private companion object {
        const val BEARER = "Bearer "
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val TOKEN_EXPIRE_TIME = longPreferencesKey("token_expire_time")
        val CURRENT_PATH = stringPreferencesKey("current_path")
        val SELECTED_DATE_TIME = stringPreferencesKey("selected_date_time")
        val defaultPath = Path(
            departureStation = Station(
                id = "1000",
                name = Name("Taipei", "臺北"),
                county = Name("Taipei", "臺北")
            ),
            arrivalStation = Station(
                id = "1210",
                name = Name("Hsinchu", "新竹"),
                county = Name("Hsinchu", "新竹")
            ),
        )
    }
}