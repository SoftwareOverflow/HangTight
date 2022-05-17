package com.softwareoverflow.hangtight.ui.util.workout.media

import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection

interface IWorkoutSoundManager {

    fun toggleSound(soundOn: Boolean)

    fun getCurrentSound(): Boolean

    fun playSound(sound: WorkoutSound)

    fun playSound(workoutSection: WorkoutSection)

    fun onDestroy()
}
