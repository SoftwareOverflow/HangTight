package com.softwareoverflow.hangtight.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.softwareoverflow.hangtight.repository.room.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao : BaseDao<WorkoutEntity> {

    @Query("SELECT * FROM Workouts")
    fun getAllWorkouts() : Flow<List<WorkoutEntity>>
}