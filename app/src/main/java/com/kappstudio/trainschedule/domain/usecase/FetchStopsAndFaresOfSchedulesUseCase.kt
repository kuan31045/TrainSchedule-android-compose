package com.kappstudio.trainschedule.domain.usecase

import com.kappstudio.trainschedule.data.Result
import com.kappstudio.trainschedule.data.toTrainSchedule
import com.kappstudio.trainschedule.domain.model.TrainSchedule
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchStopsAndFaresOfSchedulesUseCase @Inject constructor(
    private val trainRepository: TrainRepository,
) {
    suspend operator fun invoke(schedules: List<TrainSchedule>): Result<List<TrainSchedule>> =
        withContext(Dispatchers.Default) {

            val date = trainRepository.selectedDateTime.first().toLocalDate()
            val newSchedules: MutableList<TrainSchedule> = mutableListOf()

            schedules.forEach { schedule ->
                val result = trainRepository.fetchTimetables(schedule.path)

                when (result) {
                    is Result.Success -> {}
                    is Result.Fail -> return@withContext result
                    else -> return@withContext result as Result.Error
                }

                delay((200L..300L).random())

                val fares = trainRepository.fetchFares(schedule.path)
                val timeTable = result.data.first { timetable ->
                    timetable.trainInfoDto.trainNo == schedule.train.number
                }

                newSchedules.add(
                    timeTable.toTrainSchedule(
                        price = fares?.firstOrNull { fare ->
                            timeTable.trainInfoDto.direction == fare.direction
                                    && timeTable.trainInfoDto.trainTypeCode.toInt() == fare.trainType
                        }?.fares?.first()?.price ?: 0,
                        date = date
                    )
                )
            }
            Result.Success(newSchedules)
        }
}