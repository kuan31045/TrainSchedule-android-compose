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
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.getNowDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class TrainUiState(
    val trainSchedule: TrainSchedule = TrainSchedule(train = Train(""), stops = emptyList()),
    val trainShortName: String = "",
    val delay: Int = 0,
    val isFinished: Boolean = false,
)

@HiltViewModel
class TrainViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TrainUiState(
            trainShortName = savedStateHandle[NavigationArgs.TRAIN_STRING]!!,
        )
    )
    val uiState: StateFlow<TrainUiState> = _uiState.asStateFlow()

    val dateTimeState: StateFlow<LocalDateTime> = trainRepository.selectedDateTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getNowDateTime(),
    )

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Loading)
        private set

    init {
        getTrain()
    }

    private fun updateTime() {
        viewModelScope.launch {
            val delay = trainRepository.fetchTrainDelay(uiState.value.trainSchedule.train.number)
            if (delay != null) {
                _uiState.update { currentState ->
                    currentState.copy(delay = delay)
                }
            }
            val isFinished =
                getNowDateTime() > uiState.value.trainSchedule.stops.last().arrivalTime.plusMinutes(
                    uiState.value.delay.toLong()
                )

            _uiState.update { currentState ->
                currentState.copy(
                    isFinished = isFinished
                )
            }
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
                    updateTime()
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