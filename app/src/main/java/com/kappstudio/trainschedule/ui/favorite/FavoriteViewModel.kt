package com.kappstudio.trainschedule.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappstudio.trainschedule.data.toPath
import com.kappstudio.trainschedule.data.toPathEntity
import com.kappstudio.trainschedule.domain.model.Path
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUiState(val paths: List<Path> = listOf())

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
) : ViewModel() {
    val uiState: StateFlow<FavoriteUiState> =
        trainRepository.getAllPathsStream()
            .map {
                FavoriteUiState(it.map { pathEntity -> pathEntity.toPath() })
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FavoriteUiState(),
            )

    fun deletePath(path: Path) {
        viewModelScope.launch {
            trainRepository.deletePath(path.toPathEntity())
        }
    }

    fun saveCurrentPath(path: Path) {
        viewModelScope.launch {
            trainRepository.saveCurrentPath(path)
         }
    }
}