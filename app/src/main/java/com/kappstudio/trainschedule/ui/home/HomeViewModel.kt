package com.kappstudio.trainschedule.ui.home

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.AppTheme
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
import com.kappstudio.trainschedule.domain.model.Line
import com.kappstudio.trainschedule.domain.model.Name
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.repository.PreferenceRepository
import com.kappstudio.trainschedule.util.getNowDateTime
import com.kappstudio.trainschedule.util.lineMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject

data class HomeUiState(
    val stations: Map<Name, List<Station>> = emptyMap(),
    val selectedStationType: SelectedType = SelectedType.DEPARTURE,
    val selectedCatalog: Name = Name(),
    val timeType: SelectedType = SelectedType.DEPARTURE,
    val trainMainType: TrainMainType = TrainMainType.ALL,
    val canTransfer: Boolean = false
) {
    val stationsNameOfSelectedCatalog = stations[selectedCatalog]?.map { it.name } ?: emptyList()
}

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
    private val trainRepository: TrainRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val appThemeState: StateFlow<AppTheme> = preferenceRepository.appThemeCode.map {
        enumValues<AppTheme>()[it]
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppTheme.DEFAULT,
    )

    val dynamicColorState: StateFlow<Boolean> = preferenceRepository.isDynamicColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true,
    )

    val pathState: StateFlow<Path> = trainRepository.currentPath.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Path(),
    )

    val dateTimeState: StateFlow<LocalDateTime> = trainRepository.selectedDateTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = getNowDateTime(),
    )

    val stationState: StateFlow<List<Station>> = trainRepository.getAllStationsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val lineState: StateFlow<List<Line>> = trainRepository.getAllLinesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    var loadingState: LoadingStatus by mutableStateOf(LoadingStatus.Loading)
        private set

    init {
        saveDateTime(getNowDateTime(), 0)
        fetchStationsAndLines()
    }

    fun saveAppThemePreference(code: Int) {
        viewModelScope.launch {
            preferenceRepository.saveAppThemePreference(code)
        }
    }

    fun saveDynamicColorPreference(isDynamic: Boolean) {
        viewModelScope.launch {
            preferenceRepository.saveDynamicColorPreference(isDynamic)
        }
    }

    fun setupStationList() {

        val stationOfLineMap = mutableMapOf<Name, List<Station>>()

        lineMap.forEach { (name, idPair) ->
            stationOfLineMap[name] =
                lineState.value.filter { it.id == idPair.first || it.id == idPair.second }
                    .flatMap { it.stations }
        }

        val stationOfCountryMap = stationState.value.groupBy { it.county }.minus(Name())

        _uiState.update { currentState ->
            currentState.copy(
                stations = stationOfCountryMap + stationOfLineMap
            )
        }
    }

    fun selectCatalog(catalog: Name) {
        _uiState.update { currentState ->
            currentState.copy(selectedCatalog = catalog)
        }
    }

    fun selectStation(stationName: Name) {
        val catalog = uiState.value.selectedCatalog
        val station =
            uiState.value.stations[catalog]?.first { station -> station.name == stationName }

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
            selectCatalog(getCurrentPathCatalog())
        }
    }

    fun saveDateTime(dateTime: LocalDateTime, ordinal: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                timeType = enumValues<SelectedType>()[ordinal]
            )
        }
        viewModelScope.launch {
            trainRepository.saveSelectedDateTime(dateTime)
        }
    }

    fun changeSelectedStation(selectedType: SelectedType) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedStationType = selectedType
            )
        }

        selectCatalog(getCurrentPathCatalog())
    }

    fun swapPath() {
        savePath(
            Path(
                departureStation = pathState.value.arrivalStation,
                arrivalStation = pathState.value.departureStation
            )
        )
    }

    private fun getCurrentPathCatalog(): Name {
        val station = when (uiState.value.selectedStationType) {
            SelectedType.DEPARTURE -> pathState.value.departureStation
            SelectedType.ARRIVAL -> pathState.value.arrivalStation
        }

        return if (station.county != Name()) {
            station.county
        } else {
            uiState.value.stations.keys.firstOrNull { key ->
                uiState.value.stations[key]?.contains(station) ?: false
            } ?: Name()
        }
    }

    fun setTransfer() {
        _uiState.update { currentState ->
            currentState.copy(
                canTransfer = uiState.value.canTransfer.not()
            )
        }
    }

    fun selectTrainType(ordinal: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                trainMainType = enumValues<TrainMainType>()[ordinal]
            )
        }
    }

    fun fetchStationsAndLines() {
        loadingState = LoadingStatus.Loading
        viewModelScope.launch {
            val result = trainRepository.fetchStationsAndLines()
            loadingState = when (result) {
                is Result.Success -> {
                    setupStationList()
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