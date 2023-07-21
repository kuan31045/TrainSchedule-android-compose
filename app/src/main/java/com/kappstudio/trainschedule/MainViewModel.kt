package com.kappstudio.trainschedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _titleState = MutableStateFlow(Screen.Home.name)
    val titleState: StateFlow<String> = _titleState.asStateFlow()

    fun updateTitle(title: String) {
        _titleState.update { title }
    }
}