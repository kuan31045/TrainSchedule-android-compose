package com.kappstudio.trainschedule.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
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
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class TripDetailUiState(
    val trip: Trip = Trip(),
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Done)
        private set

    val dateTimeState: StateFlow<LocalDateTime> = trainRepository.selectedDateTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getNowDateTime(),
    )

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
        if (dateTimeState.value.toLocalDate() > LocalDate.now()) {
            return
        }
        val newSchedules = uiState.value.trip.trainSchedules.map {
            it.copy(train = it.train.copy(delay = trainRepository.fetchTrainDelay(it.train.number)))
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
            val result = trainRepository.fetchStopsOfSchedules(
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