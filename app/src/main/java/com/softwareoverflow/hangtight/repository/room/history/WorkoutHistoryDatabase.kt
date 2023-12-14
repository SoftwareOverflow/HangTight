package com.softwareoverflow.hangtight.repository.room.history

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutHistoryDao

@Database(entities = [WorkoutHistoryEntity::class], version = 1)
@TypeConverters(HistoryConverters::class)
abstract class WorkoutHistoryDatabase : RoomDatabase() {

    abstract val workoutHistoryDao: WorkoutHistoryDao
}