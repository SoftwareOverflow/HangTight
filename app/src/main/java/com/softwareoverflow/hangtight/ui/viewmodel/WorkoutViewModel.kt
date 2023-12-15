package com.softwareoverflow.hangtight.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import com.softwareoverflow.hangtight.ui.history.write.IHistorySaver
import com.softwareoverflow.hangtight.ui.util.getFormattedDuration
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSectionWithTime
import com.softwareoverflow.hangtight.ui.util.workout.getFormattedDuration
import com.softwareoverflow.hangtight.ui.util.workout.getTimedSections
import com.softwareoverflow.hangtight.ui.util.workout.media.WorkoutMediaManager
import com.softwareoverflow.hangtight.ui.util.workout.timer.IWorkoutTimerListener
import com.softwareoverflow.hangtight.ui.util.workout.timer.WorkoutTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.time.DurationUnit

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val workoutMediaManager: WorkoutMediaManager,
    private val historySaver: IHistorySaver
) : ViewModel(),
    IWorkoutTimerListener {

    private var isInitialized = false

    private val _showWarmUpWarning =
        MutableStateFlow(sharedPrefs.getBoolean(SharedPreferencesManager.showWarmUpWarning, true))
    val showWarmUpWarning: StateFlow<Boolean> get() = _showWarmUpWarning

    private lateinit var workout: Workout

    private val prepTime = sharedPrefs.getInt(SharedPreferencesManager.prepTime, 10)

    private lateinit var timer: WorkoutTimer

    private lateinit var _uiState: MutableStateFlow<WorkoutUiState>
    val uiState: StateFlow<WorkoutUiState> get() = _uiState

    private var _isWorkoutFinished = MutableStateFlow(false)
    val isWorkoutFinished: StateFlow<Boolean> get() = _isWorkoutFinished

    fun initialize(workout: Workout) {
        if (!isInitialized) {
            this.workout = workout

            val firstSection = workout.getTimedSections(prepTime).first()
            _uiState = MutableStateFlow(
                WorkoutUiState(
                    currentSection = firstSection,
                    timeLeftInSection = firstSection.durationSeconds,
                    timeLeftInWorkout = workout.getFormattedDuration(),
                )
            )

            timer = WorkoutTimer(workout, this, prepTime, workoutMediaManager, historySaver)

            isInitialized = true

            if(!showWarmUpWarning.value)
                timer.start()
        }
    }

    fun closeWarmUpDialog(neverShowAgain: Boolean){
        sharedPrefs.edit().apply {
            putBoolean(SharedPreferencesManager.showWarmUpWarning, !neverShowAgain)
            apply()
        }

        _showWarmUpWarning.value = false
        timer.start()
    }

    fun rewind() {
        timer.rewindSection()
    }

    fun skipSection() {
        timer.skipSection()
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
        timer.toggleSound(!_uiState.value.isMuted)
    }

    fun togglePause() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
        timer.togglePause(_uiState.value.isPaused)
    }

    override fun onTimeChange(timeLeftInSection: Int, timeLeftInWorkout: Int) {
        val currentSection = _uiState.value.currentSection

        var progress =
            (timeLeftInSection.toFloat() / currentSection.durationSeconds.toFloat())
        if (currentSection.section != WorkoutSection.Hang) {
            progress = 1f - progress
        }

        _uiState.value = _uiState.value.copy(
            timeLeftInSection = timeLeftInSection,
            timeLeftInWorkout = timeLeftInWorkout.getFormattedDuration(DurationUnit.SECONDS),
            currentSectionProgress = progress
        )
    }

    override fun onSectionChange(section: WorkoutSectionWithTime) {
        _uiState.value = _uiState.value.copy(currentSection = section)
    }

    override fun onFinish() {
        _isWorkoutFinished.value = true
    }

    override fun onCleared() {
        timer.cancel()
        super.onCleared()
    }
}