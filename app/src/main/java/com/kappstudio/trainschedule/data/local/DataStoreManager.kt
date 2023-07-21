package com.kappstudio.trainschedule.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DataStoreManager {
    private const val TOKEN_PREFERENCE_NAME = "token_preferences"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = TOKEN_PREFERENCE_NAME
    )
    fun createDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}