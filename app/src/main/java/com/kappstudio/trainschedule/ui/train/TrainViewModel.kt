package com.kappstudio.trainschedule.ui.train

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.DATE_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainUiState(
    val train: Train = Train(""),
    val trainShortName: String = "",
    val date: String = "",
)

@HiltViewModel
class TrainViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TrainUiState(
            trainShortName = savedStateHandle[NavigationArgs.TRAIN_STRING]!!,
            date = savedStateHandle[DATE_STRING]!!
        )
    )
    val uiState: StateFlow<TrainUiState> = _uiState.asStateFlow()

    init {
        getTrain()
    }

    fun getTrain() {
        viewModelScope.launch {
            val trainNumber = uiState.value.trainShortName.split("-").last()

        }
    }
}