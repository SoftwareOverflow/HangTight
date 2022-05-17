package com.softwareoverflow.hangtight.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class WorkoutCreatorViewModel : ViewModel() {

    private var isInitialized = false

    private var _workout: MutableStateFlow<Workout> = MutableStateFlow(Workout())
    val workout: StateFlow<Workout> get() = _workout

    private var inputErrors = MutableStateFlow(InputErrors())
    val anyInputErrors = inputErrors.map { it.any() }

    fun initialize(workout: Workout) {
        if (!isInitialized) {
            _workout.value = workout
            isInitialized = true
        }
    }

    fun onWorkoutSaved(workout: Workout) {
        _workout.value = workout
        SnackbarManager.showMessage("Workout Saved Successfully!")
    }


    fun setHangTime(hang: Int) {
        _workout.value = _workout.value.copy(hangTime = hang)
        inputErrors.value = inputErrors.value.copy(hangError = false)
    }

    fun setRestTime(rest: Int) {
        _workout.value = _workout.value.copy(restTime = rest)
        inputErrors.value = inputErrors.value.copy(restError = false)
    }

    fun setNumReps(numReps: Int) {
        _workout.value = _workout.value.copy(numReps = numReps)
        inputErrors.value = inputErrors.value.copy(numRepsError = false)
    }

    fun setNumSets(numSets: Int) {
        _workout.value = _workout.value.copy(numSets = numSets)
        inputErrors.value = inputErrors.value.copy(numSetsError = false)
    }

    fun setRecoverTime(recover: Int) {
        _workout.value = _workout.value.copy(recoverTime = recover)
        inputErrors.value = inputErrors.value.copy(recoverError = false)
    }

    fun setHangError(){ inputErrors.value = inputErrors.value.copy(hangError = true) }
    fun setRestError(){ inputErrors.value = inputErrors.value.copy(restError = true) }
    fun setNumRepsError() { inputErrors.value = inputErrors.value.copy(numRepsError = true) }
    fun setNumSetsError() { inputErrors.value = inputErrors.value.copy(numSetsError = true) }
    fun setRecoverError() { inputErrors.value = inputErrors.value.copy(recoverError = true) }

    data class InputErrors(
        val hangError: Boolean = false,
        val restError: Boolean = false,
        val numRepsError: Boolean = false,
        val numSetsError: Boolean = false,
        val recoverError: Boolean = false
    ) {
        fun any(): Boolean =
            hangError || restError || numRepsError || numSetsError || recoverError
    }
}