package com.kappstudio.trainschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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

            if (appThemeState.value != null && dynamicColorState.value != null) {
                TrainScheduleTheme(
                    darkTheme = when (appThemeState.value) {
                        AppTheme.DEFAULT -> isSystemInDarkTheme()
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                        else -> isSystemInDarkTheme()
                    },
                    dynamicColor = dynamicColorState.value ?: false
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        TrainApp()
                    }
                }
            }
        }
    }
}