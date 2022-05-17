package com.softwareoverflow.hangtight.ui.util.workout.media

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import com.softwareoverflow.hangtight.R
import com.softwareoverflow.hangtight.ui.SharedPreferencesManager
import com.softwareoverflow.hangtight.ui.util.workout.WorkoutSection
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class WorkoutMediaManager @Inject constructor(
    @ApplicationContext context: Context,
    sharedPreferences: SharedPreferences
) :
    IWorkoutSoundManager, IWorkoutVibrateManager {

    private val soundPool: SoundPool

    private val vibrator = context.getSystemService<Vibrator>()

    private var playSound = true
    private val playSound321 = sharedPreferences.getBoolean(SharedPreferencesManager.sound321, true)
    private var vibrate = sharedPreferences.getBoolean(SharedPreferencesManager.vibrate, true)

    private var isReady = false

    private val sound321: Int
    private val soundWorkStart: Int
    private val soundWorkoutComplete: Int
    private val soundRestStart: Int
    private val soundRecoverStart: Int

    init {
        val audioAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttrs)
            .build()
            .apply {
                setOnLoadCompleteListener { _, _, status ->
                    if (status == 0)
                        isReady = true
                }
            }

        soundWorkoutComplete = soundPool.load(context, R.raw.fanfare_workout_complete, 1)
        soundWorkStart = soundPool.load(context, R.raw.ding_work, 1)
        sound321 = soundPool.load(context, R.raw.bong_321_work, 1)
        soundRestStart = soundPool.load(context, R.raw.ding_rest, 1)
        soundRecoverStart = soundPool.load(context, R.raw.ding_rest, 1)
    }


    override fun getCurrentSound() = playSound

    override fun toggleSound(soundOn: Boolean) {
        playSound = soundOn
    }

    /**
     * Helper function to allow mapping of [WorkoutSection] to [WorkoutSound]
     */
    override fun playSound(workoutSection: WorkoutSection) {
        var shouldVibrate = true
        when (workoutSection) {
            WorkoutSection.Hang -> playSound(WorkoutSound.SOUND_WORK_START)
            WorkoutSection.Rest -> playSound(WorkoutSound.SOUND_REST_START)
            WorkoutSection.Recover -> playSound(WorkoutSound.SOUND_RECOVER_START)
            else -> {
                shouldVibrate = false
            }
        }

        if (shouldVibrate)
            vibrate()
    }

    override fun playSound(sound: WorkoutSound) {
        if (!isReady || !playSound)
            return

        when (sound) {
            WorkoutSound.SOUND_REST_START ->
                soundPool.play(soundRestStart, 1f, 1f, 9, 0, 1f)
            WorkoutSound.SOUND_RECOVER_START ->
                soundPool.play(soundRecoverStart, 1f, 1f, 9, 0, 1f)
            WorkoutSound.SOUND_321 -> {
                if(playSound321)
                    soundPool.play(sound321, 0.7f, 0.7f, 9, 1, 2f)
            }
            WorkoutSound.SOUND_WORK_START ->
                soundPool.play(soundWorkStart, 1f, 1f, 10, 0, 1f)
            WorkoutSound.SOUND_WORKOUT_COMPLETE ->
                soundPool.play(soundWorkoutComplete, 1f, 1f, 10, 0, 1f)
        }
    }

    override fun onDestroy() {
        soundPool.release()
    }

    override fun vibrate() {

        if (vibrate)
            vibrator?.let {
                vibrator.vibrate(VibrationEffect.createOneShot(100, 255))
            }
    }

    override fun toggleVibrate(vibrate: Boolean) {
        this.vibrate = vibrate
    }

    override fun isVibrateOn() = vibrate
}