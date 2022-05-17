package com.softwareoverflow.hangtight.ui.util.workout.timer

import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSectionWithTime

interface IWorkoutTimerListener {
    fun onTimeChange(timeLeftInSection: Int, timeLeftInWorkout: Int)
    fun onSectionChange(section: WorkoutSectionWithTime)
    fun onFinish()
}