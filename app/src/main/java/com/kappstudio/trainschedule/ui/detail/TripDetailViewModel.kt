package com.kappstudio.trainschedule.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

data class TripDetailUiState(
    val trip: Trip = Trip(),
    val date: LocalDate = LocalDate.now(),
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    fun setTrip(trip: Trip) {
        _uiState.update { currentState ->
            currentState.copy(trip = trip)
        }
    }
}