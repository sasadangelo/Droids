/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * A Music instance represents a streamed audio file. The interface supports pausing, resuming
 * and so on. When you are done with using the Music instance you have to dispose it via the
 * [dispose] method.
 *
 * Music instances are created via [Audio.newMusic].
 *
 * @author mzechner
 */
interface Music {
    /**
     * Starts the play back of the music stream. In case the stream was paused this will resume the
     * play back. In case the music stream is finished playing this will restart the play back.
     */
    fun play()

    /**
     * Stops a playing or paused Music instance. Next time play() is invoked the Music will start
     * from the beginning.
     */
    fun stop()

    /**
     * Pauses the play back. If the music stream has not been started yet or has finished playing
     * a call to this method will be ignored.
     */
    fun pause()

    /**
     * Sets whether the music stream is looping. This can be called at any time, whether the stream
     * is playing.
     *
     * @param looping whether to loop the stream
     */
    fun setLooping(looping: Boolean)

    /**
     * Sets the volume of this music stream. The volume must be given in the range [0,1] with 0
     * being silent and 1 being the maximum volume.
     */
    fun setVolume(volume: Float)

    /**
     * @return whether this music stream is playing.
     */
    fun isPlaying(): Boolean

    /**
     * @return whether this music stream is stopped.
     */
    fun isStopped(): Boolean

    /**
     * @return whether the music stream is playing.
     */
    fun isLooping(): Boolean

    /**
     * Needs to be called when the Music is no longer needed.
     */
    fun dispose()
}
