package com.kappstudio.trainschedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

data class MainUiState(
    val title: String,
    val currentScreen: Screen
)

class MainViewModel : ViewModel() {

    private val _titleState = MutableStateFlow(Screen.Home.name)
    val titleState: StateFlow<String> = _titleState.asStateFlow()

    private val _currentScreenState = MutableStateFlow(Screen.Home)
    val currentScreenState: StateFlow<Screen> = _currentScreenState.asStateFlow()

    fun setCurrentScreen(screen: Screen) {
        _currentScreenState.update { screen }
        Timber.d("----- Current Screen: ${currentScreenState.value} -----")

        _titleState.update {
            when (currentScreenState.value) {
                else -> currentScreenState.value.name
            }
        }
    }
}