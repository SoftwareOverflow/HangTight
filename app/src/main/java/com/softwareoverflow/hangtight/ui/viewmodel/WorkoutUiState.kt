package com.softwareoverflow.hangtight.ui.viewmodel

import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSectionWithTime

data class WorkoutUiState(
    val currentSection: WorkoutSectionWithTime,
    val currentSectionProgress: Float = 0f,

    val timeLeftInSection: Int = currentSection.durationSeconds,
    val timeLeftInWorkout: String,

    val isMuted: Boolean = false,
    val isPaused: Boolean = false,
)

