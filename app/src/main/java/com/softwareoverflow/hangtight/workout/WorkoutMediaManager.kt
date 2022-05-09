package com.softwareoverflow.hangtight.workout

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.IntegerRes

class WorkoutMediaManager constructor(val context: Context, @IntegerRes val soundId: Int) :
    IWorkoutSoundManager {

    private var _soundPool: SoundPool

    private var playSound = true

    private var isReady = false

    private val sound: Int

    init {
        val audioAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        _soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttrs)
            .build()
            .apply {
                setOnLoadCompleteListener { _, _, status ->
                    if (status == 0)
                        isReady = true
                    //else
                }
            }

        sound = _soundPool.load(context, soundId, 1)
    }

    override fun getCurrentSound() = playSound

    override fun toggleSound(soundOn: Boolean) {
        playSound = soundOn
    }

    override fun playSound() {
        if (!isReady || !playSound)
            return

        _soundPool.play(sound, 1f, 1f, 10, 0, 1f)
    }

    override fun onDestroy() {
        _soundPool.release()
    }
}