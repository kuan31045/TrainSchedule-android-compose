package com.kappstudio.trainschedule.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kappstudio.trainschedule.data.remote.TrainApi
import com.kappstudio.trainschedule.data.toStation
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.remote.dto.TokenDto

class TrainRepositoryImpl @Inject constructor(
    private val api: TrainApi,
    private val dataStore: DataStore<Preferences>
) : TrainRepository {

    override suspend fun getStations(): Result<List<Station>> {
        return try {
            val result = api.getStations(getAccessToken())
            Timber.d("getStations success = $result")

            Result.Success(result.stations.map { it.toStation() })
        } catch (e: Exception) {
            Timber.w("getStations exception = ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun getAccessToken(): String {

        suspend fun getLocalToken(): TokenDto {
            val preferences = dataStore.data.first()
            return TokenDto(
                accessToken = preferences[ACCESS_TOKEN] ?: "",
                expiresIn = preferences[TOKEN_EXPIRE_TIME] ?: 0
            )
        }

        suspend fun getNewToken() {
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

        if (getLocalToken().expiresIn < System.currentTimeMillis()) {
            getNewToken()
        }
        return getLocalToken().accessToken
    }




    private companion object {
        const val BEARER = "Bearer "
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val TOKEN_EXPIRE_TIME = longPreferencesKey("token_expire_time")
    }
}
