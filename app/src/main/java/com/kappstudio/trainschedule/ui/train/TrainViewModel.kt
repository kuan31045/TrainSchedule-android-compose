package com.kappstudio.trainschedule.ui.train

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.StationLiveBoard
import com.kappstudio.trainschedule.domain.model.Train
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.TrainType
import com.kappstudio.trainschedule.util.getNowDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class RunningStatus {
    NOT_YET,
    RUNNING,
    FINISH
}

data class TrainUiState(
    val trainSchedule: TrainSchedule = TrainSchedule(
        train = Train(number = "", type = TrainType.UNKNOWN),
        stops = emptyList()
    ),
    val trainShortName: String = "",
    val runningStatus: RunningStatus = RunningStatus.NOT_YET,
    val delay: Long = 0,
    val liveBoards: List<StationLiveBoard> = emptyList(),
    val trainIndex: Int = 0,
    val currentTime: LocalDateTime = getNowDateTime(),
)

@HiltViewModel
class TrainViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val trainShortName: String = savedStateHandle[NavigationArgs.TRAIN_STRING]!!
    private val trainNumber = trainShortName.split("-").last()

    private val _uiState = MutableStateFlow(
        TrainUiState(
            trainShortName = trainShortName
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
        fetchTrain()
    }

    fun fetchTrain() {
        loadingState = LoadingStatus.Loading
        viewModelScope.launch {
            val result = trainRepository.fetchTrainSchedule(
                trainNumber = trainNumber,
            )

            fetchInitialDelay()

            loadingState = when (result) {
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            trainSchedule = result.data
                        )
                    }
                    fetchStationLiveBoard()
                    LoadingStatus.Done
                }

                is Result.Fail -> {
                    LoadingStatus.Error(result.stringRes)
                }

                is Result.Error -> {
                    LoadingStatus.Error(R.string.api_maintenance)
                }

                else -> {
                    LoadingStatus.Loading
                }
            }
        }
    }

    private fun fetchInitialDelay() {
        viewModelScope.launch {
            val delay = trainRepository.fetchTrainDelay(trainNumber)?.toLong() ?: 0
            _uiState.update { currentState ->
                currentState.copy(delay = delay)
            }
        }
    }

    private fun fetchStationLiveBoard() {
        viewModelScope.launch {
            checkRunningStatus()
            while (uiState.value.runningStatus == RunningStatus.RUNNING) {

                val liveBoardResult = trainRepository.fetchStationLiveBoardOfTrain(
                    trainNumber = uiState.value.trainSchedule.train.number,
                )

                val currentTime = getNowDateTime()

                if (liveBoardResult.isNotEmpty()) {
                    val index = uiState.value.trainSchedule.stops.indexOfFirst {
                        it.station.id == liveBoardResult.first().stationId
                    }
                    val indexByTime = getTrainIndexByTime()
                    val delay = liveBoardResult.first().delay

                    _uiState.update { currentState ->
                        currentState.copy(
                            delay = delay,
                            liveBoards = liveBoardResult,
                            trainIndex = if (index > indexByTime) getTrainIndexByTime(delay) else index,
                            currentTime = currentTime
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            trainIndex = getTrainIndexByTime(),
                            currentTime = currentTime
                        )
                    }
                }

                checkRunningStatus()
                delay((20000L..30000L).random())
            }
        }
    }

    private fun checkRunningStatus() {
        val currentTime = getNowDateTime()
        val notYet =
            currentTime < uiState.value.trainSchedule.stops.first().departureTime.minusMinutes(
                5
            )

        val isFinished =
            currentTime > uiState.value.trainSchedule.stops.last().arrivalTime.plusMinutes(
                uiState.value.delay
            )

        _uiState.update { currentState ->
            currentState.copy(
                runningStatus = when {
                    notYet -> RunningStatus.NOT_YET
                    isFinished -> RunningStatus.FINISH
                    else -> RunningStatus.RUNNING
                },
                trainIndex = if (isFinished) {
                    currentState.trainSchedule.stops.size - 1
                } else {
                    currentState.trainIndex
                }
            )
        }
    }

    private fun getTrainIndexByTime(delay: Long = 0): Int {
        val time = getNowDateTime().minusMinutes(delay)
        val index = uiState.value.trainSchedule.stops.indexOfFirst { stop ->
            time <= stop.departureTime
        }

        return if (index < 0) uiState.value.trainSchedule.stops.size - 1 else index
    }
}