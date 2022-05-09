package com.softwareoverflow.hangtight.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

// N.B. migrated from SQLiteOpenHelper implementation

@Entity(tableName = "workouts")
class WorkoutEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id : Int? = null,

    @NotNull
    @ColumnInfo(name = "_title")
    var name: String,

    @NotNull
    @ColumnInfo(name = "_description")
    var description: String = "",

    @ColumnInfo(name = "_hang")
    @NotNull
    var hangTime: Int?,

    @NotNull
    @ColumnInfo(name = "_rest")
    var restTime: Int?,

    @NotNull
    @ColumnInfo(name = "_reps")
    var numReps: Int?,

    @NotNull
    @ColumnInfo(name = "_sets")
    var numSets: Int?,

    @ColumnInfo(name = "_recover")
    var recoverTime: Int?,

    /**
     * UNUSED NOW. Adding in to migrate from existing SQL to Room
     */
    @ColumnInfo(name= "_time")
    var _time : Int? = null
)