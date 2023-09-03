package com.kappstudio.trainschedule.domain.usecase

import com.kappstudio.trainschedule.domain.model.Trip
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.toTrainSchedule
import com.kappstudio.trainschedule.util.addDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fetch train timetables and fares, and merge them into trip list.
 */
class FetchDirectTripsUseCase @Inject constructor(
    private val trainRepository: TrainRepository,
) {
    suspend operator fun invoke(): Result<List<Trip>> = withContext(Dispatchers.Default) {

        val path = trainRepository.currentPath.first()
        val date = trainRepository.selectedDateTime.first().toLocalDate()

        val result = trainRepository.fetchTimetables()
        val fares = trainRepository.fetchFares()
        val trips: MutableList<Trip> = mutableListOf()

        when (result) {

            is Result.Success -> {
                for (timetable in result.data) {
                    val startTime = timetable.stopTimes.first().departureTime.addDate(date)
                    val endTime = timetable.stopTimes.last().arrivalTime.addDate(date)
                    trips.add(
                        Trip(
                            path = path,
                            startTime = startTime,
                            endTime = if (endTime >= startTime) endTime else endTime.plusDays(1),
                            trainSchedules = listOf(
                                timetable.toTrainSchedule(
                                    price = fares?.firstOrNull { fare ->
                                        timetable.trainInfoDto.direction == fare.direction
                                                && timetable.trainInfoDto.trainTypeCode.toInt() == fare.trainType
                                    }?.fares?.first()?.price ?: 0,
                                    date = date
                                )
                            )
                        )
                    )
                }
                Result.Success(trips)
            }

            is Result.Loading -> Result.Loading

            is Result.Fail -> result

            is Result.Error -> result
        }
    }
}