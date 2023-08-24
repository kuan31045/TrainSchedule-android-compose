package com.kappstudio.trainschedule.util

import androidx.annotation.StringRes
import com.kappstudio.trainschedule.R

enum class TrainFlag(
    @StringRes val flagName: Int,
) {
    DAILY_FLAG(flagName = R.string.daily),
    BIKE_FLAG(flagName = R.string.bike),
    WHEEL_CHAIR_FLAG(flagName = R.string.wheel_chair),
    BREAST_FEED_FLAG(flagName = R.string.breast_feed);

    companion object {
        fun getFlagList(daily: Int, bike: Int, wheel: Int, breastfeeding: Int): List<TrainFlag> {
            return listOfNotNull(
                if (daily != 0) DAILY_FLAG else null,
                if (bike != 0) BIKE_FLAG else null,
                if (wheel != 0) WHEEL_CHAIR_FLAG else null,
                if (breastfeeding != 0) BREAST_FEED_FLAG else null
            )
        }
    }
}