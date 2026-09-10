/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * A Sound is a short audio clip that can be played numerous times in parallel. It's completely
 * loaded into memory so only load small audio files. Call the [dispose] method when
 * you're done using the Sound.
 *
 * Sound instances are created via a call to [Audio.newSound].
 *
 * @author mzechner
 */
interface Sound {
    /**
     * Plays the sound. If the sound is already playing, it will be played again, concurrently.
     * @param volume the volume in the range [0,1]
     */
    fun play(volume: Float)

    /**
     * Releases all the resources.
     */
    fun dispose()
}
