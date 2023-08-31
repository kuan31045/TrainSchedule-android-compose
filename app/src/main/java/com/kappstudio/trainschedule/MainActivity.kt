package com.kappstudio.trainschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kappstudio.trainschedule.ui.TrainApp
import com.kappstudio.trainschedule.ui.theme.TrainScheduleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val viewModel: MainViewModel = viewModel()
            val appThemeState = viewModel.appThemeState.collectAsState()
            val dynamicColorState = viewModel.dynamicColorState.collectAsState()

            TrainScheduleTheme(
                darkTheme = when (appThemeState.value) {
                    AppTheme.DEFAULT -> isSystemInDarkTheme()
                    AppTheme.LIGHT -> false
                    AppTheme.DARK -> true
                },
                dynamicColor = dynamicColorState.value
            ) {
                TrainApp()
            }
        }
    }
}