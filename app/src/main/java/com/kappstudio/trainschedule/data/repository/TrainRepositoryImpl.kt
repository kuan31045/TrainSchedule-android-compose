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
import com.kappstudio.trainschedule.data.toPath
import com.kappstudio.trainschedule.data.toPathEntity
import com.kappstudio.trainschedule.data.toTrainSchedule
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

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

    override suspend fun fetchStations(): Result<List<Station>> {
        return try {
            val result = api.getStations(fetchAccessToken())
            Result.Success(result.stations.map { it.toStation() })
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

    override suspend fun searchTrips(
        date: String,
    ): Result<List<Trip>> {
        return try {
            val result = api.getTrainTimetable(
                token = fetchAccessToken(),
                departureStationId = currentPath.first().departureStation.id,
                arrivalStationId = currentPath.first().arrivalStation.id,
                date = date
            )
            delay(500)
            val fares = api.getODFare(
                token = fetchAccessToken(),
                departureStationId = currentPath.first().departureStation.id,
                arrivalStationId = currentPath.first().arrivalStation.id,
            ).odFares

            Result.Success(result.trainTimetables.map { timeTable ->
                Trip(
                    path = currentPath.first(),
                    departureTime = timeTable.stopTimes.first().departureTime,
                    arrivalTime = timeTable.stopTimes.last().arrivalTime,
                    trainSchedules = listOf(
                        timeTable.toTrainSchedule(
                            price = fares.first { fare ->
                                timeTable.trainInfoDto.direction == fare.direction
                                        && timeTable.trainInfoDto.trainTypeCode.toInt() == fare.trainType
                            }.fares.first().price
                        )
                    )
                )
            })
        } catch (e: Exception) {
            Timber.w("getTrainTimetable exception = ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun searchTransferTrips(date: String): Result<List<Trip>> {
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

    override suspend fun getTrainDelayTime(trainNumber: String): Int? {
        return try {
            val result = api.getTrainLiveBoard(fetchAccessToken(), trainNumber)
            result.trainLiveBoards?.first()?.delayTime
        } catch (e: Exception) {
            Timber.w("getTrainLiveBoard exception = ${e.message}")
            null
        }
    }

    private companion object {
        const val BEARER = "Bearer "
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val TOKEN_EXPIRE_TIME = longPreferencesKey("token_expire_time")
        val CURRENT_PATH = stringPreferencesKey("current_path")
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