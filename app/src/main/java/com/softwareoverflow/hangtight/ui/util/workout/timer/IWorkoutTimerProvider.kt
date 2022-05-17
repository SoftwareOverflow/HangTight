package com.softwareoverflow.hangtight.ui.util.workout.timer

interface IWorkoutTimerProvider {
    fun startTimer()
    fun cancelTimer()
    fun createTimer(millis: Long)
}