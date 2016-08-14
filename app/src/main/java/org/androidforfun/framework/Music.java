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
package org.androidforfun.framework;

/**
 * <p>
 * A Music instance represents a streamed audio file. The interface supports pausing, resuming
 * and so on. When you are done with using the Music instance you have to dispose it via the
 * {@link #dispose()} method.
 * </p>
 *
 * <p>
 * Music instances are created via {@link Audio#newMusic(String)}.
 * </p>
 *
 * @author mzechner
 */
public interface Music {
    /**
     * Starts the play back of the music stream. In case the stream was paused this will resume the
     * play back. In case the music stream is finished playing this will restart the play back.
     */
    void play();

    /**
     * Stops a playing or paused Music instance. Next time play() is invoked the Music will start
     * from the beginning.
     */
    void stop();

    /**
     * Pauses the play back. If the music stream has not been started yet or has finished playing
     * a call to this method will be ignored.
     */
    void pause();

    /**
     * Sets whether the music stream is looping. This can be called at any time, whether the stream
     * is playing.
     *
     * @param looping whether to loop the stream
     */
    void setLooping(boolean looping);

    /**
     * Sets the volume of this music stream. The volume must be given in the range [0,1] with 0
     * being silent and 1 being the maximum volume.
     *
     * @param volume
     */
    void setVolume(float volume);

    /**
     * @return whether this music stream is playing.
     */
    boolean isPlaying();

    /**
     * @return whether this music stream is stopped.
     */
    boolean isStopped();

    /**
     * @return whether the music stream is playing.
     */
    boolean isLooping();

    /**
     * Needs to be called when the Music is no longer needed.
     */
    void dispose();
}
