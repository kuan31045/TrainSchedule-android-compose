package com.kappstudio.trainschedule.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trainRepository: TrainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

}