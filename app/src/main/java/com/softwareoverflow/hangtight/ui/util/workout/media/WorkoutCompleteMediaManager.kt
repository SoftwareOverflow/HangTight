package com.softwareoverflow.hangtight.ui.util.workout.media

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import javax.inject.Inject

@ViewModelScoped
class WorkoutCompleteMediaManager @Inject constructor(
    @ApplicationContext context: Context, private val sharedPreferences: SharedPreferences
) {

    private val soundPool: SoundPool

    private var isReady = false
    private var playSound =
        sharedPreferences.getBoolean(SharedPreferencesManager.playWorkoutSounds, true)

    private val soundWorkoutComplete: Int

    init {
        val audioAttrs =
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_MEDIA).build()

        soundPool =
            SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttrs).build().apply {
                setOnLoadCompleteListener { _, _, status ->
                    if (status == 0) isReady = true
                }
            }

        soundWorkoutComplete = soundPool.load(context, R.raw.fanfare_workout_complete, 1)
    }

    suspend fun playWorkoutCompleteSound(onSoundPlayed: () -> Unit) {
        if(!playSound){
            onSoundPlayed()
            return
        }

        val maxRetryCount = 3
        val retryDelay = 100L

        var attempt = 1
        while (attempt <= maxRetryCount) {
            if (!isReady) {
                attempt++
                delay(retryDelay * attempt)
            } else {
                soundPool.play(soundWorkoutComplete, 1f, 1f, 9, 0, 1f)
                onSoundPlayed()
                break
            }
        }
    }

    fun cancel() {
        soundPool.release()
    }

}