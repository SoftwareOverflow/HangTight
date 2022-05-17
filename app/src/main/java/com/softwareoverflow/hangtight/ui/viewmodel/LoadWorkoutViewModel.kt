package com.softwareoverflow.hangtight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.hangtight.data.Workout
import com.softwareoverflow.hangtight.repository.IWorkoutRepository
import com.softwareoverflow.hangtight.ui.util.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadWorkoutViewModel @Inject constructor(private val repo: IWorkoutRepository) : ViewModel(){

    private val _allWorkouts : MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
    val allWorkouts :StateFlow<List<Workout>> get() = _allWorkouts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            repo.getAllWorkouts().collect {
                _allWorkouts.value = it

                _isLoading.value = false
            }
        }
    }

    fun deleteWorkout(workout: Workout){
        viewModelScope.launch {
            repo.deleteWorkout(workout)

            SnackbarManager.showMessage("Workout Deleted")
        }
    }
}