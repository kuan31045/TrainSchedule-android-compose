package com.kappstudio.trainschedule.domain.model

import android.content.Context
import com.kappstudio.trainschedule.util.getNowDateTime
import com.kappstudio.trainschedule.R
import com.kappstudio.trainschedule.util.detailFormat
import java.time.Duration
import java.time.LocalDateTime

data class Trip(
    val path: Path = Path(),
    val startTime: LocalDateTime = getNowDateTime(),
    val endTime: LocalDateTime = getNowDateTime(),
    val trainSchedules: List<TrainSchedule> = emptyList(),
) {
    val transferCount: Int = trainSchedules.size - 1
    val durationMinutes: Long = Duration.between(startTime, endTime).toMinutes()
    val totalPrice: Int = trainSchedules.sumOf { it.price }

    fun toSummary(context: Context, hasTitle:Boolean): String {
        val departure = context.resources.getString(R.string.departure)
        val title = if(hasTitle) {
            path.getTitle() + "\n" + startTime.format(detailFormat) + " " + departure + "\n\n"
        }else {
            ""
        }

        return title + trainSchedules.joinToString(separator = "\n") {
                    it.toSummary(context)
                }
    }
}