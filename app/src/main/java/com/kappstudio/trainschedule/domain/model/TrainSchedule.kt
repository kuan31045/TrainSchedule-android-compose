package com.kappstudio.trainschedule.domain.model

import android.content.Context
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.util.timeFormatter

data class TrainSchedule(
    val path: Path = Path(),
    val train: Train,
    val price: Int = 0,
    val stops: List<Stop> = emptyList(),
) {
    fun getStartTime() = stops.first().departureTime
    fun getEndTime() = stops.last().arrivalTime

    fun toSummary(context: Context): String {
        val departure = context.resources.getString(R.string.departure)
        val arrival = context.resources.getString(R.string.arrival)

        return context.resources.getString(train.type.trainName) + train.number + "\n" +
                stops.first().station.name.localize() + " " + getStartTime()
            .format(timeFormatter) + " " + departure + "\n" + stops.last().station.name.localize() + " " + getEndTime()
            .format(timeFormatter) + " " + arrival + "\n"
    }
}