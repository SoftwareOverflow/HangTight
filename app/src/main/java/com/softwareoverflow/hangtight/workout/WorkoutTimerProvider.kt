package com.softwareoverflow.hangtight.workout

import android.os.CountDownTimer

class WorkoutTimerProvider(
    val onTimerFinish: () -> Unit,
    val onTimerTick: (tickInterval: Long, millisUntilFinished: Long) -> Unit
) : IWorkoutTimerProvider {

    lateinit var _timer: CountDownTimer

    override fun startTimer(){
        try {
            _timer.start()
        } catch (e: UninitializedPropertyAccessException) {
            // Do nothing
        }
    }

    override fun cancelTimer() {
        try {
            _timer.cancel()
        } catch (e: UninitializedPropertyAccessException) {
            // Do nothing
        }
    }

    override fun createTimer(millis: Long) {
        val tickInterval = 1000L

        // Prevent duplicate timers being created
        cancelTimer()

        _timer = object : CountDownTimer(millis, tickInterval) {
            override fun onFinish() {
                onTimerFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                onTimerTick(tickInterval, millisUntilFinished)
            }
        }
    }
}