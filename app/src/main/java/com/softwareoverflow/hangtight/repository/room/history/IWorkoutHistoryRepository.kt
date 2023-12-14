package com.softwareoverflow.hangtight.repository.room.history

import com.softwareoverflow.hangtight.data.WorkoutHistory
import java.time.LocalDate

interface IWorkoutHistoryRepository {

    suspend fun createOrUpdate(workoutHistory: WorkoutHistory)

    suspend fun getAllHistory() : List<WorkoutHistory>

    suspend fun getHistoryBetweenDates(from: LocalDate, to: LocalDate) : List<WorkoutHistory>
}