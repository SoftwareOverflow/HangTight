package com.softwareoverflow.hangtight.ui.util.workout.media

interface IWorkoutVibrateManager {
    fun vibrate()
    fun toggleVibrate(vibrate: Boolean)
    fun isVibrateOn() : Boolean
}