package com.softwareoverflow.hangtight.workout

interface IWorkoutTimerProvider {
    fun startTimer()
    fun cancelTimer()
    fun createTimer(millis: Long)
}