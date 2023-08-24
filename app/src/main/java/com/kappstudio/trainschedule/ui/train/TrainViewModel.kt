package com.kappstudio.trainschedule.ui.train

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.DATE_STRING
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.checkIsRunning
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TrainUiState(
    val trainSchedule: TrainSchedule = TrainSchedule(train = Train(""), stops = emptyList()),
    val trainShortName: String = "",
    val date: String = "",
    val isRunning: Boolean = false,
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

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Loading)
        private set

    init {
        getTrain()
    }

    private fun updateIsRunning() {
        val isRunning = checkIsRunning(
            date = uiState.value.date,
            startTime = uiState.value.trainSchedule.getStartTime(),
            endTime = uiState.value.trainSchedule.getEndTime(),
            isOverNight = uiState.value.trainSchedule.train.isOverNight
        )

        _uiState.update { currentState ->
            currentState.copy(isRunning = isRunning)
        }
    }

    fun getTrain() {
        val trainNumber = uiState.value.trainShortName.split("-").last()
        viewModelScope.launch {
            val result =
                trainRepository.fetchTrainSchedule(
                    trainNumber = trainNumber,
                )

            loadingState = when (result) {
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            trainSchedule = result.data
                        )
                    }
                    updateIsRunning()
                    LoadingStatus.Done
                }

                is Result.Fail -> {
                    LoadingStatus.Error(result.error)
                }

                is Result.Error -> {
                    LoadingStatus.Error(result.exception.toString())
                }

                else -> {
                    LoadingStatus.Loading
                }
            }
        }
    }
}