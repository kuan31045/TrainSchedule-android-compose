package com.kappstudio.trainschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kappstudio.trainschedule.ui.TrainApp
import com.kappstudio.trainschedule.ui.theme.TrainScheduleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainScheduleTheme {
                TrainApp()
            }
        }
    }
}