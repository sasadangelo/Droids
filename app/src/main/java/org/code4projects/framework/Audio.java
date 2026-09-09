/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework;

/**
 * This interface encapsulates the creation and management of audio resources. It creates sound
 * effects via the {@link Sound} interface and play music streams via the {@link Music} interface.
 *
 * <p>
 * All resources created via this interface have to be disposed as soon as they are no longer used.
 * </p>
 *
 * @author mzechner
 */
public interface Audio {
    /**
     * Creates a new {@link Music} instance which is used to play back a music stream from a file.
     * Currently supported formats are WAV, MP3 and OGG. The Music instance has to be disposed if
     * it is no longer used via the {@link Music#dispose()} method.
     *
     * @param filename the file name to play
     * @return the new Music or null if the Music could not be loaded
     */
    Music newMusic(String filename);

    /** <p>
     * Creates a new {@link Sound} which is used to play back audio effects such as gun shots or
     * explosions. The Sound's audio data is retrieved from the file specified. Note that the
     * complete audio data is loaded into RAM. You should therefore not load big audio files with
     * this methods. The current upper limit for decoded audio is 1 MB.
     * </p>
     *
     * <p>
     * Currently supported formats are WAV, MP3 and OGG.
     * </p>
     *
     * <p>
     * The Sound has to be disposed if it is no longer used via the {@link Sound#dispose()} method.
     * </p>
     *
     * @return the new Sound
     */
    Sound newSound(String filename);
}
