package com.softwareoverflow.hangtight.ui.util.workout.timer

import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.history.write.IHistorySaver
import com.softwareoverflow.hangtight.ui.util.ListObjectIterator
import com.softwareoverflow.hangtight.ui.util.workout.getDurationMillis
import com.softwareoverflow.hangtight.ui.util.workout.getTimedSections
import com.softwareoverflow.hangtight.ui.util.workout.media.WorkoutMediaManager
import com.softwareoverflow.hangtight.ui.util.workout.media.WorkoutSound
import timber.log.Timber

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
        timerProvider = WorkoutTimerProvider(
            onTimerFinish = {
                mediaManager.playSound(WorkoutSound.SOUND_WORKOUT_COMPLETE)
                observer.onFinish()

                cancel()
            },
            onTimerTick = { tickInterval, millisUntilFinished ->
                observer.onTimeChange(
                    (millisRemainingInSection / 1000).toInt(),
                    (millisecondsRemaining / 1000).toInt()
                )

                Timber.d("History WorkoutTimer OnTimerTick: $tickInterval")
                historySaver.addHistory((tickInterval / 1000).toInt(), currentSection.section)

                if (millisRemainingInSection in 1..3000L)
                    mediaManager.playSound(WorkoutSound.SOUND_321)

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
        millisecondsRemaining =
            ((millisecondsRemaining + 999) / 1000) * 1000 // Round up to the nearest second (in millis) to prevent the frequent polling of the timer getting out of sync

        if (millisecondsRemaining <= 0) {
            mediaManager.playSound(WorkoutSound.SOUND_WORKOUT_COMPLETE)

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
            millisecondsRemaining =
                ((millisecondsRemaining + 999) / 1000) * 1000 // Display lags behind by 1s - round up
            millisRemainingInSection =
                ((millisRemainingInSection + 999) / 1000) * 1000// Display lags behind by 1s - round up

            getTimerProvider().createTimer(millisecondsRemaining)

            isRunning = true
            getTimerProvider().startTimer()
        }
    }

    fun toggleSound(soundOn: Boolean) {
        mediaManager.toggleSound(soundOn)
    }

    private fun startNextWorkoutSection() {
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
        mediaManager.onDestroy()
        historySaver.write()
    }

    private fun getCurrentSectionTime() = currentSection.durationSeconds * 1000L

    private fun getTimerProvider() = timerProvider
}
