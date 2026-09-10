/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import java.io.IOException

import android.app.Activity
import android.media.AudioManager
import android.media.SoundPool

import org.code4projects.framework.Audio
import org.code4projects.framework.Music
import org.code4projects.framework.Sound

/*
 * Implement the Audio interface for Android. The class create a SoundPool to manage brief Sound
 * file like explosion, beep, etc. Usually these file are completely uploaded in memory so make sure
 * they are small. It will use MediaPlayer class to play music stream.
 *
 * @author mzechner
 */
class AndroidAudio(activity: Activity) : Audio {
    private val assets = activity.assets
    private val soundPool = SoundPool(20, AudioManager.STREAM_MUSIC, 0)

    init {
        activity.volumeControlStream = AudioManager.STREAM_MUSIC
    }

    /*
     * Implementation of the factory method used to create a Music file. A Music file is implemented
     * by the class AndroidMusic that delegate the behaviour to Android MediaPlayer class.
     */
    override fun newMusic(filename: String): Music {
        try {
            val assetDescriptor = assets.openFd(filename)
            return AndroidMusic(assetDescriptor)
        } catch (e: IOException) {
            throw RuntimeException("Couldn't load music '$filename'")
        }
    }

    /*
     * Implementation of the factory method used to create a Sound file. A Sound file is implemented
     * by the class AndroidSound that delegate the behaviour to Android SoundPool class.
     */
    override fun newSound(filename: String): Sound {
        try {
            val assetDescriptor = assets.openFd(filename)
            val soundId = soundPool.load(assetDescriptor, 0)
            return AndroidSound(soundPool, soundId)
        } catch (e: IOException) {
            throw RuntimeException("Couldn't load sound '$filename'")
        }
    }
}
