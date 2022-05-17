package com.softwareoverflow.hangtight.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutDao

@Database(entities = [WorkoutEntity::class], version = 3)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract val workoutDao: WorkoutDao

}

// This migration is purely to convert from old SQLite implementation to Room.
// No changes are necessary but version number needs incrementing
val MIGRATION_2_3 = Migration(2, 3) {}