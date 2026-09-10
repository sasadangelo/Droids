/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.media.SoundPool

import org.code4projects.framework.Sound

/*
 * This class represents a brief sound like beep, explosion, etc. A sound file can be played at
 * a specific volume. The Android SoundPool class will be used to manage these sounds.
 *
 * @author mzechner
 */
class AndroidSound(private val soundPool: SoundPool, private val soundId: Int) : Sound {

    /*
     * Plays the sound file at the specified volume.
     */
    override fun play(volume: Float) {
        soundPool.play(soundId, volume, volume, 0, 0, 1f)
    }

    /*
     * Disposes a sound file.
     */
    override fun dispose() {
        soundPool.unload(soundId)
    }
}
