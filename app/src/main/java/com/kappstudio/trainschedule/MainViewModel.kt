package com.kappstudio.trainschedule

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.domain.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class AppTheme(@StringRes val stringRes: Int) {
    DEFAULT(stringRes = R.string.system_default),
    LIGHT(stringRes = R.string.light_theme),
    DARK(stringRes = R.string.dark_theme)
}

@HiltViewModel
class MainViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository,
) : ViewModel() {

    val appThemeState: StateFlow<AppTheme?> = preferenceRepository.appThemeCode.map {
        enumValues<AppTheme>()[it]
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val dynamicColorState: StateFlow<Boolean?> = preferenceRepository.isDynamicColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}