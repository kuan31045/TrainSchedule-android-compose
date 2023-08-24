package com.kappstudio.trainschedule.ui.home

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.domain.model.Station
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.util.LoadingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class HomeUiState(
    val stationsOfCounty: Map<Name, List<Station>> = emptyMap(),
    val selectedStationType: SelectedType = SelectedType.DEPARTURE,
    val selectedCounty: Name = Name(),
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val timeType: SelectedType = SelectedType.DEPARTURE,
    val trainMainType: TrainMainType = TrainMainType.ALL,
    val canTransfer: Boolean = false,
)

enum class TrainMainType(@StringRes val text: Int) {
    ALL(R.string.all),
    EXPRESS(R.string.express),
    LOCAL(R.string.local)
}

enum class SelectedType(@StringRes val text: Int) {
    DEPARTURE(R.string.departure),
    ARRIVAL(R.string.arrival)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trainRepository: TrainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val pathState: StateFlow<Path> = trainRepository.currentPath.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Path(),
    )

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Loading)
        private set

    init {
        getStations()
    }

    fun selectCounty(county: Name) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCounty = county
            )
        }
    }

    fun selectStation(stationName: Name) {
        val county = uiState.value.selectedCounty
        val station =
            uiState.value.stationsOfCounty[county]?.first { station -> station.name == stationName }

        val path = when (uiState.value.selectedStationType) {
            SelectedType.DEPARTURE -> {
                Path(
                    departureStation = station ?: pathState.value.departureStation,
                    arrivalStation = pathState.value.arrivalStation
                )
            }

            SelectedType.ARRIVAL -> {
                Path(
                    departureStation = pathState.value.departureStation,
                    arrivalStation = station ?: pathState.value.arrivalStation
                )
            }
        }
        savePath(path)
    }

    private fun savePath(path: Path) {
        viewModelScope.launch {
            trainRepository.saveCurrentPath(path)
            selectCounty(getCurrentPathCounty())
        }
    }

    fun changeSelectedStation(selectedType: SelectedType) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedStationType = selectedType
            )
        }

        selectCounty(getCurrentPathCounty())
    }

    fun swapPath() {
        savePath(
            Path(
                departureStation = pathState.value.arrivalStation,
                arrivalStation = pathState.value.departureStation
            )
        )
    }

    private fun getCurrentPathCounty(): Name {
        return when (uiState.value.selectedStationType) {
            SelectedType.DEPARTURE -> pathState.value.departureStation.county
            SelectedType.ARRIVAL -> pathState.value.arrivalStation.county
        }
    }

    fun setDateTime(date: LocalDate, time: LocalTime, ordinal: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                date = date,
                time = time,
                timeType =  enumValues<SelectedType>()[ordinal]
            )
        }
    }

    fun setTransfer(){
        _uiState.update { currentState ->
            currentState.copy(
                canTransfer = uiState.value.canTransfer.not()
            )
        }
    }

    fun selectTrainType(ordinal:Int) {
        _uiState.update { currentState ->
            currentState.copy(
                trainMainType = enumValues<TrainMainType>()[ordinal]
            )
        }
    }

    private fun getStations() {
        viewModelScope.launch {
            val result = trainRepository.fetchStationsAndLines()
            loadingState = when (result) {
                is Result.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            stationsOfCounty = result.data.groupBy { it.county }.minus(Name())
                        )
                    }
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
