package com.softwareoverflow.hangtight.repository.room.history

import com.softwareoverflow.hangtight.data.WorkoutHistory

fun List<WorkoutHistory>.toEntity() : List<WorkoutHistoryEntity> {
    return this.map { it.toEntity() }
}

fun WorkoutHistory.toEntity(): WorkoutHistoryEntity {
    return WorkoutHistoryEntity(
        this.hangTime, this.restTime, this.recoverTime, this.date
    )
}

fun List<WorkoutHistoryEntity>.toDto() : List<WorkoutHistory> {
    return this.map { it.toDto() }
}

fun WorkoutHistoryEntity.toDto(): WorkoutHistory {
    return WorkoutHistory(this.hangTime, this.restTime, this.recoverTime, this.date)
}