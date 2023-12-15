package com.softwareoverflow.hangtight.repository.room.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "workout_history")
data class WorkoutHistoryEntity(

    @ColumnInfo(name = "_hang_time")
    var hangTime: Int = 0,

    @ColumnInfo(name = "_rest_time")
    var restTime: Int = 0,

    @ColumnInfo(name = "_recover_time")
    var recoverTime: Int = 0,

    @PrimaryKey
    @ColumnInfo(name = "_date_string")
    var date: LocalDate = LocalDate.now() // This will be converted by the converter to a string in the form "yyyyMMdd"
    )