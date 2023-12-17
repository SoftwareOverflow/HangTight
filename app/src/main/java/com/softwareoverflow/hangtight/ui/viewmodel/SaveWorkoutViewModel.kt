package com.softwareoverflow.hangtight.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.logging.FirebaseManager
import com.softwareoverflow.hangtight.repository.IWorkoutRepository
import com.softwareoverflow.hangtight.ui.util.findActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveWorkoutViewModel @Inject constructor(
    private val repo: IWorkoutRepository,
    val firebaseManager: FirebaseManager,
    private val billingViewModel: BillingViewModel
) : ViewModel() {

    private val _savedWorkouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
    val savedWorkouts: StateFlow<List<Workout>> get() = _savedWorkouts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _savedWorkout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    val savedWorkout: StateFlow<Workout?> get() = _savedWorkout

    init {
        _savedWorkout.value = null
        _isLoading.value = true

        viewModelScope.launch {
            _savedWorkouts.value = repo.getAllWorkouts().first()

            _isLoading.value = false
        }
    }

    fun saveWorkout(
        workout: Workout,
        name: String,
        description: String,
        idToOverwrite: Int? = null
    ) {
        val workoutToSave = workout.copy(id = idToOverwrite, name = name, description = description)
        viewModelScope.launch {
            val id = repo.createOrUpdateWorkout(workoutToSave)
            _savedWorkout.value = workoutToSave.copy(id = id.toInt())
        }
    }

    fun launchUpgrade(context: Context){
        context.findActivity()?.let {
            billingViewModel.purchasePro(it)
        }
    }
}