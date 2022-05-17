package com.softwareoverflow.hangtight.repository

import com.softwareoverflow.hangtight.data.Workout
import kotlinx.coroutines.flow.Flow

interface IWorkoutRepository {

    suspend fun getAllWorkouts() : Flow<List<Workout>>

    suspend fun deleteWorkout(obj: Workout)

    suspend fun createOrUpdateWorkout(dto: Workout) : Long
}