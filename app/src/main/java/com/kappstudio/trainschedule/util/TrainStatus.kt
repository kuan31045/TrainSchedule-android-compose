package com.kappstudio.trainschedule.util

import androidx.annotation.StringRes
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.domain.model.Train

enum class TrainStatus(
    @StringRes val status: Int,
) {
    EXPECTED(status = R.string.predict_departure),
    ON_TIME(status = R.string.on_time),
    DELAY(status = R.string.delay);

    companion object {
        fun getTrainStatus(train: Train): TrainStatus {
            return when (train.delay) {
                null -> EXPECTED
                0 -> ON_TIME
                else -> DELAY
            }
        }
    }
}