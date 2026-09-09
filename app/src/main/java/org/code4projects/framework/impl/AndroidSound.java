/*
 *  Copyright (C) 2016 Mario Zechner
 *  This file is part of Framework for book Beginning Android Games.
 *
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework.impl;

import android.media.SoundPool;

import org.androidforfun.framework.Sound;

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
