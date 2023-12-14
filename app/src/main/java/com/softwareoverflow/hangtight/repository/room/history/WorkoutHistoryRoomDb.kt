package com.softwareoverflow.hangtight.repository.room.history

import com.softwareoverflow.hangtight.data.WorkoutHistory
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutHistoryDao
import java.time.LocalDate
import javax.inject.Inject

class WorkoutHistoryRoomDb @Inject constructor(private val historyDao: WorkoutHistoryDao) :
    IWorkoutHistoryRepository {

    override suspend fun createOrUpdate(workoutHistory: WorkoutHistory) {

        val dateString = HistoryConverters.historyDateFormat.format(workoutHistory.date)

        val existingData = historyDao.getHistoryBetweenDates(dateString, dateString).singleOrNull()
            ?: WorkoutHistoryEntity()

        workoutHistory.hangTime += existingData.hangTime
        workoutHistory.restTime += existingData.restTime
        workoutHistory.recoverTime += existingData.recoverTime

        historyDao.createOrUpdate(workoutHistory.toEntity())
    }

    override suspend fun getAllHistory(): List<WorkoutHistory> {
        return historyDao.getAllHistory().map { it.toDto() }
    }

    override suspend fun getHistoryBetweenDates(
        from: LocalDate, to: LocalDate
    ): List<WorkoutHistory> {
        val fromString = HistoryConverters.historyDateFormat.format(from)
        val toString = HistoryConverters.historyDateFormat.format(to)
        return historyDao.getHistoryBetweenDates(fromString, toString).map { item -> item.toDto() }
    }
}