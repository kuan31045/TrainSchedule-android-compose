package com.kappstudio.trainschedule.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.kappstudio.trainschedule.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferenceRepository {
    override val appThemeCode: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e("Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val theme = preferences[APP_THEME] ?: 0
            theme
        }

    override val isDynamicColor: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Timber.e("Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val isDynamicTheme = preferences[IS_DYNAMIC_COLOR] ?: false
            isDynamicTheme
        }

    override suspend fun saveAppThemePreference(code: Int) {
        dataStore.edit { preferences ->
            preferences[APP_THEME] = code
        }
    }

    override suspend fun saveDynamicColorPreference(isDynamic: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLOR] = isDynamic
        }
    }

    private companion object {
        val APP_THEME = intPreferencesKey("APP_THEME")
        val IS_DYNAMIC_COLOR = booleanPreferencesKey("IS_DYNAMIC_COLOR")
    }
}