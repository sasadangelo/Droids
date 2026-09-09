/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import org.code4projects.framework.Music;

/*
 * This class represents a stream of music to play. A music file can be played, stopped, paused.
 * It can be played in loop and its volume can be regulated. A music file is managed by
 * Android class MediaPlayer.
 *
 * @author mzechner
 */
public class AndroidMusic implements Music, OnCompletionListener {
    MediaPlayer mediaPlayer;
    boolean isPrepared = false;

    /*
     * Initializes a music file to play.
     */
    public AndroidMusic(AssetFileDescriptor assetDescriptor) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
                    assetDescriptor.getStartOffset(),
                    assetDescriptor.getLength());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load music");
        }
    }

    /*
     * Disposes a music file.
     */
    public void dispose() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
    }

    /*
     * Checks if music file is played in loop.Disposes a music file.
     */
    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    /*
     * Checks if music file is playing.
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /*
     * Checks if music file is stopped.
     */
    public boolean isStopped() {
        return !isPrepared;
    }

    /*
     * Pauses the music file.
     */
    public void pause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    /*
     * Plays the music file.
     */
    public void play() {
        if (mediaPlayer.isPlaying())
            return;

        try {
            synchronized (this) {
                if (!isPrepared)
                    mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Sets the music file to be played in loop.
     */
    public void setLooping(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
    }

    /*
     * Sets the volume of the music file.
     */
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    /*
     * Stops the music file.
     */
    public void stop() {
        mediaPlayer.stop();
        synchronized (this) {
            isPrepared = false;
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            isPrepared = false;
        }
    }
}
