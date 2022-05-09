package com.softwareoverflow.hangtight.workout

import com.softwareoverflow.hangtight.helper.WorkoutHelper

/**
 * Helper class to store a section and duration (seconds) for that section.
 * Used to help transform a [Workout]s [WorkoutSet]s into a more useful list for timers
 *
 * @param section The [WorkoutSection] for this timed section
 * @param durationSeconds duration in seconds for this timed section
 * @param set the current workout set number
 * @param rep the current repetition number
 */
data class WorkoutSectionWithTime(
    val section: WorkoutHelper.WorkoutSection,
    val durationSeconds: Int,
    val set: WorkoutSectionCounter,
    val rep: WorkoutSectionCounter,
)

data class WorkoutSectionCounter(
    val index: Int,
    val total: Int,
)