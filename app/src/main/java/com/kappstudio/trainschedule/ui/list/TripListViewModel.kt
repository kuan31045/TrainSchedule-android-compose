package com.kappstudio.trainschedule.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.domain.usecase.FetchDirectTripsUseCase
import com.kappstudio.trainschedule.domain.usecase.FetchTransferTripsUseCase
import com.kappstudio.trainschedule.ui.home.SelectedType
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.CAN_TRANSFER_BOOLEAN
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_TYPE_INT
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TRAIN_TYPE_INT
import com.kappstudio.trainschedule.util.LoadingStatus
import com.kappstudio.trainschedule.util.TrainType
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

data class TripListUiState(
    val trips: List<Trip> = emptyList(),
    val isFavorite: Boolean = false,
    val initialTripIndex: Int = 0,
    val canTransfer: Boolean = false,
    val filteredTrainTypes: List<TrainType> = emptyList(),
    val isFiltering: Boolean = false,
)

@HiltViewModel
class TripListViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    private val fetchDirectTripsUseCase: FetchDirectTripsUseCase,
    private val fetchTransferTripsUseCase: FetchTransferTripsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val timeType: SelectedType =
        enumValues<SelectedType>()[savedStateHandle[TIME_TYPE_INT]!!]

    private val trips: MutableStateFlow<List<Trip>> = MutableStateFlow(emptyList())

    private val _uiState = MutableStateFlow(
        TripListUiState(
            filteredTrainTypes = TrainType.getTypes(savedStateHandle[TRAIN_TYPE_INT]!!),
            canTransfer = savedStateHandle[CAN_TRANSFER_BOOLEAN]!!
        )
    )
    val uiState: StateFlow<TripListUiState> = _uiState.asStateFlow()

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Loading)
        private set

    val currentPath: StateFlow<Path> = trainRepository.currentPath.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Path(),
    )

    val dateTimeState: StateFlow<LocalDateTime> = trainRepository.selectedDateTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getNowDateTime(),
    )

    init {
        checkFavorite()
        searchTrips()
    }

    private fun checkFavorite() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isFavorite = trainRepository.isCurrentPathFavorite()
                )
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (uiState.value.isFavorite) {
                trainRepository.deletePath(currentPath.value)
            } else {
                trainRepository.insertPath(currentPath.value)
            }
            checkFavorite()
        }
    }

    private fun setSpecifiedTimeTrip() {


        _uiState.update { currentState ->
            currentState.copy(
                initialTripIndex =
                if (timeType == SelectedType.DEPARTURE) {
                    currentState.trips.indexOfLast { it.startTime < dateTimeState.value } + 1
                } else {
                    currentState.trips.indexOfLast { it.endTime < dateTimeState.value }.let {
                        if (it > 0) it else 0
                    }
                }
            )
        }
    }

    private fun filterTrips() {
        val newTrips: List<Trip> = trips.value.filter { trip ->
            trip.trainSchedules.all { schedule -> schedule.train.type in uiState.value.filteredTrainTypes }
        }.sortedBy { it.startTime }
        _uiState.update { currentState ->
            currentState.copy(trips = newTrips)
        }
        setSpecifiedTimeTrip()
    }

    fun openFilter() {
        _uiState.update { currentState ->
            currentState.copy(isFiltering = !uiState.value.isFiltering)
        }
    }

    fun closeFilter(types: List<TrainType>) {
        _uiState.update { currentState ->
            currentState.copy(
                isFiltering = !uiState.value.isFiltering,
                filteredTrainTypes = types
            )
        }
        filterTrips()
    }

    fun searchTrips() {
        loadingState = LoadingStatus.Loading
        viewModelScope.launch {
            val result =
                if (uiState.value.canTransfer) {
                    fetchTransferTripsUseCase()
                } else {
                    fetchDirectTripsUseCase()
                }

            loadingState = when (result) {
                is Result.Success -> {
                    trips.update { result.data.sortedBy { it.endTime } }
                    _uiState.update { currentState ->
                        currentState.copy(
                            trips = trips.value
                        )
                    }
                    filterTrips()
                    LoadingStatus.Done
                }

                is Result.Fail -> {
                    LoadingStatus.Error(result.stringRes)
                }

                is Result.Error -> {
                    if (uiState.value.canTransfer) {
                        LoadingStatus.Error(R.string.web_update)
                    } else {
                        LoadingStatus.Error(R.string.api_maintenance)
                    }
                }

                else -> {
                    LoadingStatus.Loading
                }
            }
        }
    }
}