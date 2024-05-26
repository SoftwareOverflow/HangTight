package com.softwareoverflow.hangtight.ui.util.workout.timer

import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.history.write.IHistorySaver
import com.softwareoverflow.hangtight.ui.util.ListObjectIterator
import com.softwareoverflow.hangtight.ui.util.workout.getDurationMillis
import com.softwareoverflow.hangtight.ui.util.workout.getTimedSections
import com.softwareoverflow.hangtight.ui.util.workout.media.WorkoutMediaManager
import com.softwareoverflow.hangtight.ui.util.workout.media.WorkoutSound
import kotlin.math.abs

class WorkoutTimer(
    workout: Workout,
    private val observer: IWorkoutTimerListener,
    prepTime: Int,
    private val mediaManager: WorkoutMediaManager,
    private val historySaver: IHistorySaver,
) {
    private var millisecondsRemaining: Long = (workout.getDurationMillis() + prepTime * 1000L)

    private val timerProvider: IWorkoutTimerProvider

    private var isRunning = false
    private var isPaused: Boolean = false

    private val timedSections = workout.getTimedSections(prepTime)
    private val workoutSets = ListObjectIterator(timedSections.listIterator())
    private var currentSection = workoutSets.next()


    private var millisRemainingInSection = getCurrentSectionTime()


    init {
        timerProvider = WorkoutTimerProvider(100L,
            onTimerFinish = {
                observer.onFinish()

                cancel()
            },
            onTimerTick = { millisUntilFinished ->
                val thisTick = millisecondsRemaining - millisUntilFinished

                val didChangeSecond =
                    millisUntilFinished / 1000 != (millisUntilFinished + thisTick) / 1000

                millisRemainingInSection -= thisTick
                millisecondsRemaining -= thisTick


                if (didChangeSecond) {
                    historySaver.addHistory(1, currentSection.section)

                    if (millisRemainingInSection > 0) {
                        when (millisRemainingInSection / 1000) {
                            2L, 1L, 0L -> {
                                mediaManager.playSound(WorkoutSound.SOUND_321)
                            }
                        }
                    }

                    observer.onTimeChange(
                        ((millisRemainingInSection) / 1000).toInt() + 1,
                        ((millisecondsRemaining) / 1000).toInt() + 1
                    )
                }

                if (millisRemainingInSection <= 0 && millisUntilFinished > 0)
                    startNextWorkoutSection()
            }
        )

        getTimerProvider().createTimer(millisecondsRemaining)

        observer.onSectionChange(currentSection)
    }

    fun rewindSection() {
        isRunning = false
        getTimerProvider().cancelTimer()

        val currentSound = mediaManager.getCurrentSound()
        val currentVibrate = mediaManager.isVibrateOn()
        mediaManager.toggleSound(false)
        mediaManager.toggleVibrate(false)

        startPreviousWorkoutSection()

        mediaManager.toggleSound(currentSound)
        mediaManager.toggleVibrate(currentVibrate)

        if (!isPaused) {
            getTimerProvider().createTimer(millisecondsRemaining)
            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    /** Skips the current section of the workout **/
    fun skipSection() {
        millisecondsRemaining -= millisRemainingInSection
        millisRemainingInSection = 0

        if (millisecondsRemaining <= 0) {
            getTimerProvider().cancelTimer() // Cancel the timer to prevent onFinish being called multiple times
            observer.onFinish()
            return
        }

        val currentSound = mediaManager.getCurrentSound()
        val currentVibrate = mediaManager.isVibrateOn()
        mediaManager.toggleSound(false)
        mediaManager.toggleVibrate(false)

        startNextWorkoutSection()

        mediaManager.toggleSound(currentSound)
        mediaManager.toggleVibrate(currentVibrate)

        // Cancel and recreate the timer
        isRunning = false
        getTimerProvider().cancelTimer()

        if (!isPaused) {
            getTimerProvider().createTimer(millisecondsRemaining)
            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    /** Allows the pausing / resuming of the timer **/
    fun togglePause(isPaused: Boolean) {
        this.isPaused = isPaused

        if (isPaused) {
            isRunning = false
            getTimerProvider().cancelTimer()
        } else if (!isRunning) {
            getTimerProvider().createTimer(millisecondsRemaining)

            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    fun toggleSound(soundOn: Boolean) {
        mediaManager.toggleSound(soundOn)
    }

    private fun startNextWorkoutSection() {
        // Add back any possible overshoot
        millisecondsRemaining += abs(millisRemainingInSection)

        var nextSection = workoutSets.tryGetNext()

        if (nextSection == timedSections.first()) {
            nextSection = workoutSets.tryGetNext()
        }

        if (nextSection != null) {
            currentSection = nextSection
            millisRemainingInSection = getCurrentSectionTime()

            mediaManager.playSound(currentSection.section)
            mediaManager.vibrate()

            observer.onSectionChange(currentSection)
            observer.onTimeChange(
                millisRemainingInSection.toInt() / 1000,
                millisecondsRemaining.toInt() / 1000
            )
        }
    }

    private fun startPreviousWorkoutSection() {
        val previousSection = workoutSets.tryGetPrevious()
        if (previousSection != null) {
            // Add back the currently completed part of the section
            val milliSecondsToReset = getCurrentSectionTime() - millisRemainingInSection
            millisecondsRemaining += milliSecondsToReset

            currentSection = previousSection

            millisRemainingInSection = getCurrentSectionTime()
            // Now add back the section to be replayed
            millisecondsRemaining += millisRemainingInSection

            observer.onSectionChange(currentSection)
        }

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
        mediaManager.onDestroy()
        historySaver.write()
    }

    private fun getCurrentSectionTime() = currentSection.durationSeconds * 1000L

    private fun getTimerProvider() = timerProvider
}
