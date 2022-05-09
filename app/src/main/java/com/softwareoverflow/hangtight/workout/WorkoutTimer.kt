package com.softwareoverflow.hangtight.workout

import com.softwareoverflow.hangtight.helper.WorkoutHelper
import com.softwareoverflow.hangtight.util.ListObjectIterator

class WorkoutTimer(
    workout: Workout,
    private val observer: IWorkoutTimerListener,
    prepTime: Int,
) {
    private var millisecondsRemaining: Long = (workout.duration + prepTime) * 1000L

    private val timerProvider: IWorkoutTimerProvider

    private var isRunning = false
    private var isPaused: Boolean = false

    private val workoutSets = ListObjectIterator(workout.getTimedSections(prepTime).listIterator())
    private var currentSection = workoutSets.next()

    private var millisRemainingInSection = getCurrentSectionTime()

    init {
        timerProvider = WorkoutTimerProvider(
            onTimerFinish = {
                getTimerProvider().cancelTimer()
                observer.onFinish()
            },
            onTimerTick = { tickInterval, millisUntilFinished ->
                observer.onTimeChange(
                    (millisRemainingInSection / 1000).toInt(),
                    (millisecondsRemaining / 1000).toInt()
                )

                if (millisRemainingInSection <= 0 && millisUntilFinished > tickInterval)
                    startNextWorkoutSection()

                millisRemainingInSection -= tickInterval
                millisecondsRemaining -= tickInterval
            }
        )

        getTimerProvider().createTimer(millisecondsRemaining)

        observer.onSectionChange(currentSection)
    }

    fun rewindSection() {
        isRunning = false
        getTimerProvider().cancelTimer()

        val milliSecondsToReset =
            getCurrentSectionTime() - millisRemainingInSection // Amount of time already completed in section being reset
        millisecondsRemaining += milliSecondsToReset
        millisecondsRemaining =
            ((millisecondsRemaining + 999) / 1000) * 1000 // Round up to the nearest second (in millis) to prevent the frequent polling of the timer getting out of sync

        startPreviousWorkoutSection()

        if (!isPaused) {
            getTimerProvider().createTimer(millisecondsRemaining)
            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    /** Skips the current section of the workout **/
    fun skipSection() {
        millisecondsRemaining -= millisRemainingInSection
        millisecondsRemaining =
            ((millisecondsRemaining + 999) / 1000) * 1000 // Round up to the nearest second (in millis) to prevent the frequent polling of the timer getting out of sync

        if (millisecondsRemaining <= 0) {
            getTimerProvider().cancelTimer() // Cancel the timer to prevent onFinish being called multiple times
            observer.onFinish()
            return
        }

        startNextWorkoutSection()

        // Cancel and recreate the timer
        isRunning = false
        getTimerProvider().cancelTimer()

        if (!isPaused) {
            getTimerProvider().createTimer(millisecondsRemaining)
            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    /** Allows the pausing / resuming of the timer**/
    fun togglePause(isPaused: Boolean) {
        this.isPaused = isPaused

        if (isPaused) {
            isRunning = false
            getTimerProvider().cancelTimer()
        } else if (!isRunning) {
            millisecondsRemaining =
                ((millisecondsRemaining + 999) / 1000) * 1000 // Display lags behind by 1s - round up
            millisRemainingInSection =
                ((millisRemainingInSection + 999) / 1000) * 1000// Display lags behind by 1s - round up

            getTimerProvider().createTimer(millisecondsRemaining)

            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    private fun startNextWorkoutSection() {
        var nextSection = workoutSets.tryGetNext()

        if(nextSection?.section == WorkoutHelper.WorkoutSection.PREPARE){
            nextSection = workoutSets.tryGetNext()
        }

        if(nextSection != null) {
            currentSection = nextSection
            millisRemainingInSection = getCurrentSectionTime()

            observer.onSectionChange(currentSection)
            observer.onTimeChange(
                millisRemainingInSection.toInt() / 1000,
                millisecondsRemaining.toInt() / 1000
            )
        }
    }

    private fun startPreviousWorkoutSection() {
        val previousSection = workoutSets.tryGetPrevious()
        if(previousSection != null) {
            currentSection = previousSection
            millisecondsRemaining += getCurrentSectionTime()
            observer.onSectionChange(currentSection)
        }

        millisRemainingInSection = getCurrentSectionTime()
        observer.onTimeChange(
            millisRemainingInSection.toInt() / 1000,
            millisecondsRemaining.toInt() / 1000
        )
    }

    fun start() {
        isRunning = true
        getTimerProvider().startTimer()
    }

    fun cancel() {
        getTimerProvider().cancelTimer()
    }

    private fun getCurrentSectionTime() = currentSection.durationSeconds * 1000L

    private fun getTimerProvider() = timerProvider
}
