package com.softwareoverflow.hangtight.repository.room

import com.softwareoverflow.hangtight.data.Workout

fun List<WorkoutEntity>.toDto(): List<Workout> {
    return this.map { it.toDto() }
}

fun WorkoutEntity.toDto(): Workout {
    return Workout(
        id = this.id,
        name = this.name,
        description = this.description,
        hangTime = this.hangTime!!,
        restTime = this.restTime!!,
        numReps = this.numReps!!,
        numSets = this.numSets!!,
        recoverTime = this.recoverTime!!,
    )
}

fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        hangTime = this.hangTime,
        restTime = this.restTime,
        numReps = this.numReps,
        numSets = this.numSets,
        recoverTime = this.recoverTime,
    )
}