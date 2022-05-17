package com.softwareoverflow.hangtight.repository.room

import android.content.Context
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.repository.IWorkoutRepository
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutRepositoryRoomDb @Inject constructor(
    @ApplicationContext val context: Context,
    private val workoutDao: WorkoutDao
) : IWorkoutRepository {

    override suspend fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { it.toDto() }
    }

    override suspend fun deleteWorkout(obj: Workout) {
        workoutDao.delete(obj.toEntity())
    }

    override suspend fun createOrUpdateWorkout(dto: Workout): Long {
        return workoutDao.createOrUpdate(dto.toEntity())
    }
}