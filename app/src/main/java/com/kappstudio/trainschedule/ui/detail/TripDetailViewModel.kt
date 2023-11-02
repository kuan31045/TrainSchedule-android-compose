package com.kappstudio.trainschedule.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.domain.usecase.FetchStopsAndFaresOfSchedulesUseCase
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.getNowDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripDetailUiState(
    val trip: Trip = Trip(),
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    private val fetchStopsAndFaresOfSchedulesUseCase: FetchStopsAndFaresOfSchedulesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Done)
        private set

    fun setTrip(trip: Trip, isTransferTrip: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(trip = trip)
        }

        if (isTransferTrip) {
            fetchStop()
        } else {
            viewModelScope.launch { fetchTrainsDelayTime() }
        }
    }

    suspend fun fetchTrainsDelayTime() {
        if (uiState.value.trip.startTime.minusHours(4) > getNowDateTime()) {
            return
        }

        val newSchedules = uiState.value.trip.trainSchedules.map { schedule ->
            val delay = trainRepository.fetchTrainLiveBoard(
                trainNumber = schedule.train.number
            )?.delay

            schedule.copy(train = schedule.train.copy(delay = delay))
        }

        _uiState.update { currentState ->
            currentState.copy(
                trip = uiState.value.trip.copy(trainSchedules = newSchedules)
            )
        }
    }

    fun fetchStop() {
        loadingState = LoadingStatus.Loading
        viewModelScope.launch {
            val result = fetchStopsAndFaresOfSchedulesUseCase(
                uiState.value.trip.trainSchedules
            )

            loadingState = when (result) {
                is Result.Success -> {
                    val newTrip = uiState.value.trip.copy(
                        trainSchedules = result.data
                    )
                    _uiState.update { currentState ->
                        currentState.copy(trip = newTrip)
                    }
                    fetchTrainsDelayTime()
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
}