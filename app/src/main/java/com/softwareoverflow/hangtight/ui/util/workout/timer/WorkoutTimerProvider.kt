package com.softwareoverflow.hangtight.ui.util.workout.timer

import android.os.CountDownTimer

class WorkoutTimerProvider(
    val tickInterval: Long,
    val onTimerFinish: () -> Unit,
    val onTimerTick: (millisUntilFinished: Long) -> Unit
) : IWorkoutTimerProvider {

    private lateinit var timer: CountDownTimer

    override fun startTimer(){
        try {
            timer.start()
        } catch (e: UninitializedPropertyAccessException) {
            // Do nothing
        }
    }

    override fun cancelTimer() {
        try {
            timer.cancel()
        } catch (e: UninitializedPropertyAccessException) {
            // Do nothing
        }
    }

    override fun createTimer(millis: Long) {
        // Prevent duplicate timers being created
        cancelTimer()

        timer = object : CountDownTimer(millis, tickInterval) {
            override fun onFinish() {
                onTimerFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                onTimerTick(millisUntilFinished)
            }
        }
    }
}