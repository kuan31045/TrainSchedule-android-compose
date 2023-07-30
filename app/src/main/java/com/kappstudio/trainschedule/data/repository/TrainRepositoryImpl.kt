package com.kappstudio.trainschedule.data.repository

import android.util.Log
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
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Singleton

class TrainRepositoryImpl @Inject constructor(
    private val api: TrainApi,
    private val dataStore: DataStore<Preferences>,
    private val trainDb: TrainDatabase
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

    override suspend fun getAccessToken(): String {
        if (localToken.first().expiresIn < System.currentTimeMillis()) {
            try {
                //Get token from Api
                val newToken = api.getAccessToken()
                Timber.d("getToken success!")
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
            val result = api.getStations(getAccessToken())
            Timber.d("getStations success = $result")

            Result.Success(result.stations.map { it.toStation() })
        } catch (e: Exception) {
            Timber.w("getStations exception = ${e.message}")
            Result.Error(e)
        }
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
            Timber.d("getPath()=$path")

            path ?: defaultPath
        }

    override suspend fun savePath(path: Path) {
        val json = Gson().toJson(path)
        Timber.d("savePath()=$json")
        dataStore.edit { preferences ->
            preferences[CURRENT_PATH] = json
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
