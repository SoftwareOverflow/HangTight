package com.softwareoverflow.hangtight.workout

interface IWorkoutSoundManager {

    fun toggleSound(soundOn: Boolean)

    fun getCurrentSound(): Boolean

    fun playSound()

    fun onDestroy()
}