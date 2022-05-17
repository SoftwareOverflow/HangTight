package com.softwareoverflow.hangtight.repository

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.softwareoverflow.hangtight.repository.room.MIGRATION_2_3
import com.softwareoverflow.hangtight.repository.room.WorkoutDatabase
import com.softwareoverflow.hangtight.repository.room.WorkoutRepositoryRoomDb
import com.softwareoverflow.hangtight.repository.room.dao.WorkoutDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    private const val databaseName = "saved_workouts.db"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : WorkoutDatabase {
        val builder = Room.databaseBuilder(context, WorkoutDatabase::class.java, databaseName)
            .createFromAsset("default_saved_workouts.db")
            .addMigrations(MIGRATION_2_3)

        if(Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }

        return builder.build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {
    @Provides
    fun provideWorkoutDao(db: WorkoutDatabase) : WorkoutDao = db.workoutDao
}

@InstallIn(SingletonComponent::class)
@Module
abstract class DatabaseModuleBinds {

    // use the WorkoutRepositoryRoomDb for both IWorkoutRepository & IGripTypeRepository
    @Binds
    abstract fun bindWorkoutRepository(db: WorkoutRepositoryRoomDb): IWorkoutRepository
}