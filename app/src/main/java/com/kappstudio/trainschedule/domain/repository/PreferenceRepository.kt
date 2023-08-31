package com.kappstudio.trainschedule.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val appThemeCode: Flow<Int>

    val isDynamicColor: Flow<Boolean>

    suspend fun saveAppThemePreference(code: Int)

    suspend fun saveDynamicColorPreference(isDynamic: Boolean)
}