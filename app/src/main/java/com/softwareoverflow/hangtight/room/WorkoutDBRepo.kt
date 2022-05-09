package com.softwareoverflow.hangtight.room

import android.content.Context
import androidx.room.Room

class WorkoutDBRepo(val context: Context) {

    val dbInstance : WorkoutRoomDB = Room.databaseBuilder(context.applicationContext,
        WorkoutRoomDB::class.java, "saved_workouts.db")
        .addMigrations(MIGRATION_2_3)
        .build()

    suspend fun  getAllWorkouts() = dbInstance.workoutDao.getAllWorkouts()
}