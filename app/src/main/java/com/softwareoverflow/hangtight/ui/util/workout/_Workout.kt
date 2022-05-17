package com.softwareoverflow.hangtight.ui.util.workout

import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.util.getFormattedDuration
import kotlin.time.DurationUnit

enum class WorkoutSection(val nameResourceId: Int) {
    Prepare(R.string.section_prepare), Hang(R.string.section_hang), Rest(R.string.section_rest), Recover(
        R.string.section_recover
    );
}

fun Workout.getDurationMillis(): Long {
    var duration = 0

    for (setIndex in 0 until numSets) {
        for (repIndex in 0 until numReps - 1) {

            duration += this.hangTime
            duration += this.restTime
        }

        duration += this.hangTime
        duration += this.recoverTime
    }

    // No recovery on final set
    duration -= this.recoverTime

    return duration * 1000L
}

fun Workout.getTimedSections(prepTime: Int): List<WorkoutSectionWithTime> {
    val timedSections = emptyList<WorkoutSectionWithTime>().toMutableList()

    if (prepTime > 0)
        timedSections.add(
            WorkoutSectionWithTime(
                WorkoutSection.Prepare,
                prepTime,
                WorkoutSectionCounter(0, numSets),
                WorkoutSectionCounter(0, numReps)
            )
        )

    for (setIndex in 0 until numSets) {
        for (repIndex in 0 until numReps - 1) {
            timedSections.add(
                WorkoutSectionWithTime(
                    WorkoutSection.Hang,
                    hangTime,
                    WorkoutSectionCounter(setIndex, numSets),
                    WorkoutSectionCounter(repIndex, numReps)
                )
            )

            timedSections.add(
                WorkoutSectionWithTime(
                    WorkoutSection.Rest,
                    restTime,
                    WorkoutSectionCounter(setIndex, numSets),
                    WorkoutSectionCounter(repIndex, numReps)
                )
            )
        }

        timedSections.add(
            WorkoutSectionWithTime(
                WorkoutSection.Hang,
                hangTime,
                WorkoutSectionCounter(setIndex, numSets),
                WorkoutSectionCounter(numReps - 1, numReps)
            )
        )

        timedSections.add(
            WorkoutSectionWithTime(
                WorkoutSection.Recover,
                recoverTime,
                WorkoutSectionCounter(setIndex, numSets),
                WorkoutSectionCounter(numReps - 1, numReps)
            )
        )
    }

    // Don't do any recovery on the very final set as the workout is over
    timedSections.removeLast()

    return timedSections
}

/**
 * Gets the workout duration, formatted in minutes and seconds (mm:ss)
 */
fun Workout.getFormattedDuration() = getDurationMillis().getFormattedDuration(DurationUnit.MILLISECONDS)