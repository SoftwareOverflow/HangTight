package com.softwareoverflow.hangtight.workout

import com.softwareoverflow.hangtight.helper.WorkoutHelper


fun Workout.getTimedSections(prepTime: Int): List<WorkoutSectionWithTime> {
    val timedSections = emptyList<WorkoutSectionWithTime>().toMutableList()

    if (prepTime > 0)
        timedSections.add(
            WorkoutSectionWithTime(
                WorkoutHelper.WorkoutSection.PREPARE,
                prepTime,
                WorkoutSectionCounter(0, numSets),
                WorkoutSectionCounter(0, numReps)
            )
        )

    for (setIndex in 0 until numSets) {
        for (repIndex in 0 until numReps - 1) {
            timedSections.add(
                WorkoutSectionWithTime(
                    WorkoutHelper.WorkoutSection.HANG,
                    hangTime,
                    WorkoutSectionCounter(setIndex, numSets),
                    WorkoutSectionCounter(repIndex, numReps)
                )
            )

            timedSections.add(
                WorkoutSectionWithTime(
                    WorkoutHelper.WorkoutSection.REST,
                    restTime,
                    WorkoutSectionCounter(setIndex, numSets),
                    WorkoutSectionCounter(repIndex, numReps)
                )
            )
        }

        timedSections.add(
            WorkoutSectionWithTime(
                WorkoutHelper.WorkoutSection.HANG,
                hangTime,
                WorkoutSectionCounter(setIndex, numSets),
                WorkoutSectionCounter(numReps - 1, numReps)
            )
        )

        timedSections.add(
            WorkoutSectionWithTime(
                WorkoutHelper.WorkoutSection.RECOVER,
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