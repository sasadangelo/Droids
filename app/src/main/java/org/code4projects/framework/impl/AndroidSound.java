/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl;

import android.media.SoundPool;

import org.code4projects.framework.Sound;

/*
 * This class represents a brief sound like beep, explosion, etc. A sound file can be played at
 * a specific volume. The Android SoundPool class will be used to manage these sounds.
 *
 * @author mzechner
 */
public class AndroidSound implements Sound {
    int soundId;
    SoundPool soundPool;

    public AndroidSound(SoundPool soundPool, int soundId) {
        this.soundId = soundId;
        this.soundPool = soundPool;
    }

    /*
     * Plays the sound file at the specified volume.
     */
    public void play(float volume) {
        soundPool.play(soundId, volume, volume, 0, 0, 1);
    }

    /*
     * Disposes a sound file.
     */
    public void dispose() {
        soundPool.unload(soundId);
    }
}
