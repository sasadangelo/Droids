/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer

import org.code4projects.framework.Music

/*
 * This class represents a stream of music to play. A music file can be played, stopped, paused.
 * It can be played in loop and its volume can be regulated. A music file is managed by
 * Android class MediaPlayer.
 *
 * @author mzechner
 */
class AndroidMusic(assetDescriptor: AssetFileDescriptor) : Music, MediaPlayer.OnCompletionListener {
    private val mediaPlayer = MediaPlayer()
    private var isPrepared = false

    /*
     * Initializes a music file to play.
     */
    init {
        try {
            mediaPlayer.setDataSource(
                assetDescriptor.fileDescriptor, assetDescriptor.startOffset, assetDescriptor.length
            )
            mediaPlayer.prepare()
            isPrepared = true
            mediaPlayer.setOnCompletionListener(this)
        } catch (e: Exception) {
            throw RuntimeException("Couldn't load music")
        }
    }

    /*
     * Disposes a music file.
     */
    override fun dispose() {
        if (mediaPlayer.isPlaying)
            mediaPlayer.stop()
        mediaPlayer.release()
    }

    /*
     * Checks if music file is played in loop.
     */
    override fun isLooping(): Boolean = mediaPlayer.isLooping

    /*
     * Checks if music file is playing.
     */
    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    /*
     * Checks if music file is stopped.
     */
    override fun isStopped(): Boolean = !isPrepared

    /*
     * Pauses the music file.
     */
    override fun pause() {
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }

    /*
     * Plays the music file.
     */
    override fun play() {
        if (mediaPlayer.isPlaying)
            return

        try {
            synchronized(this) {
                if (!isPrepared)
                    mediaPlayer.prepare()
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Sets the music file to be played in loop.
     */
    override fun setLooping(looping: Boolean) {
        mediaPlayer.isLooping = looping
    }

    /*
     * Sets the volume of the music file.
     */
    override fun setVolume(volume: Float) {
        mediaPlayer.setVolume(volume, volume)
    }

    /*
     * Stops the music file.
     */
    override fun stop() {
        mediaPlayer.stop()
        synchronized(this) {
            isPrepared = false
        }
    }

    override fun onCompletion(player: MediaPlayer) {
        synchronized(this) {
            isPrepared = false
        }
    }
}
