package com.kappstudio.trainschedule.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object DataStoreManager {
    private const val PREFERENCE_NAME = "train_preferences"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCE_NAME
    )
    fun createDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}