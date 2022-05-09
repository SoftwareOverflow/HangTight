package com.softwareoverflow.hangtight.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM Workouts")
    suspend fun getAllWorkouts() : List<WorkoutEntity>
}