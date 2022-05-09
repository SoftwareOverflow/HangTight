package com.softwareoverflow.hangtight.room

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TempViewModel(context: Context) : ViewModel() {

    //dbHandler = new MyDBHandler(getBaseContext(), null);
    lateinit var db: WorkoutDBRepo

    lateinit var workouts: List<WorkoutEntity>

    init {
        viewModelScope.launch {
            db = WorkoutDBRepo(context.applicationContext)
            workouts = db.getAllWorkouts()
        }
    }
}