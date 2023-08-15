package com.kappstudio.trainschedule.ui.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.toPathEntity
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.ui.home.SelectedType
import com.kappstudio.trainschedule.ui.home.TrainMainType
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.CAN_TRANSFER_BOOLEAN
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.DATE_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_STRING
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TIME_TYPE_INT
import com.kappstudio.trainschedule.ui.navigation.NavigationArgs.TRAIN_TYPE_INT
import com.kappstudio.trainschedule.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripListUiState(
    val trips: List<Trip> = emptyList(),
    val date: String = "",
    val isFavorite: Boolean = false,
    val specifiedTimeTrip: Trip? = null,
)

@HiltViewModel
class TripListViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val time: String = savedStateHandle[TIME_STRING]!!
    private val timeType: SelectedType =
        enumValues<SelectedType>()[savedStateHandle[TIME_TYPE_INT]!!]
    private val trainMainType: TrainMainType =
        enumValues<TrainMainType>()[savedStateHandle[TRAIN_TYPE_INT]!!]
    private val canTransfer: Boolean = savedStateHandle[CAN_TRANSFER_BOOLEAN]!!

    private val trips: MutableStateFlow<List<Trip>> = MutableStateFlow(emptyList())

    private val _uiState = MutableStateFlow(TripListUiState(date = savedStateHandle[DATE_STRING]!!))
    val uiState: StateFlow<TripListUiState> = _uiState.asStateFlow()

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Loading)
        private set

    val currentPath: StateFlow<Path> = trainRepository.currentPath.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Path(),
    )

    init {
        searchTrips()
        checkFavorite()
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

    private fun setSpecifiedTimeTrip() {
        val arrivalTimes = uiState.value.trips.map {
            if (it.arrivalTime < it.departureTime) {
                val h = it.arrivalTime.split(":").first().toInt() + 24
                val min = it.arrivalTime.split(":").last()
                "$h:$min"
            } else {
                it.arrivalTime
            }
        }

        val arrTime = arrivalTimes.lastOrNull { it < time }?.let {
            arrivalTimes.indexOf(it)
        }

        _uiState.update { currentState ->
            currentState.copy(
                specifiedTimeTrip =
                if (timeType == SelectedType.DEPARTURE) {
                    currentState.trips.lastOrNull { it.departureTime < time }
                } else {
                    arrTime?.let { currentState.trips[it] }
                }
            )
        }
    }

    fun searchTrips() {
        viewModelScope.launch {
            val result =
                if (canTransfer) {
                    trainRepository.searchTransferTrips(date = uiState.value.date)
                } else {
                    trainRepository.searchTrips(date = uiState.value.date)
                }

            loadingState = when (result) {
                is Result.Success -> {
                    trips.update { result.data.sortedBy { it.departureTime } }
                    _uiState.update { currentState ->
                        currentState.copy(
                            trips = trips.value
                        )
                    }
                    setSpecifiedTimeTrip()
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

    fun toggleFavorite() {
        viewModelScope.launch {
            if (uiState.value.isFavorite) {
                trainRepository.deletePath(currentPath.value.toPathEntity())
            } else {
                trainRepository.insertPath(currentPath.value.toPathEntity())
            }
            checkFavorite()
        }
    }
}