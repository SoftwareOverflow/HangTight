package com.softwareoverflow.hangtight.workout

interface IWorkoutTimerListener {
    fun onTimeChange(timeLeftInSection: Int, timeLeftInWorkout: Int)
    fun onSectionChange(section: WorkoutSectionWithTime)
    fun onFinish()
}