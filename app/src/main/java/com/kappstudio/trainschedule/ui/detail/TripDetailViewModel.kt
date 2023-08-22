package com.kappstudio.trainschedule.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TripDetailUiState(
    val trip: Trip = Trip(),
    val date: String = "",
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    fun setTrip(trip: Trip, date: String) {
        _uiState.update { currentState ->
            currentState.copy(trip = trip, date = date)
        }
        viewModelScope.launch {
            fetchTrainsDelayTime()
        }
    }

    suspend fun fetchTrainsDelayTime() {
        if (uiState.value.date > LocalDate.now().toString()) {
            return
        }
        val newSchedules = uiState.value.trip.trainSchedules.map {
            it.copy(train = it.train.copy(delayTime = trainRepository.getTrainDelayTime(it.train.number)))
        }
        _uiState.update { currentState ->
            currentState.copy(
                trip = uiState.value.trip.copy(trainSchedules = newSchedules)
            )
        }
    }
}