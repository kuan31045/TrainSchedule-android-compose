package com.kappstudio.trainschedule.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
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

    val dateTimeState: StateFlow<LocalDateTime> = trainRepository.selectedDateTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getNowDateTime(),
    )

    fun setTrip(trip: Trip) {
        _uiState.update { currentState ->
            currentState.copy(trip = trip)
        }
        viewModelScope.launch {
            fetchTrainsDelayTime()
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
}