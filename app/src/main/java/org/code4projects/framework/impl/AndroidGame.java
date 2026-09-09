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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

import org.androidforfun.framework.Audio;
import org.androidforfun.framework.FileIO;
import org.androidforfun.framework.Game;
import org.androidforfun.framework.Gdx;
import org.androidforfun.framework.Graphics;
import org.androidforfun.framework.Input;
import org.androidforfun.framework.Screen;

/*
 * On Android a Game interface is implemented by an Activity. It will manage the game lifecycle and
 * also it will manage the subsystem of the game library (Graphics, FileIO, Audio and Input).
 * It will create the frame buffer.
 *
 * @author mzechner
 */
public abstract class AndroidGame extends Activity implements Game {
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    WakeLock wakeLock;

    /*
     * Initialize the Game subsystems and frame buffer. It will create also the first screen of
     * the game: the start screen.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 480 : 320;
        int frameBufferHeight = isLandscape ? 320 : 480;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);
        
        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(getAssets());
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);

        Gdx.game = this;
        Gdx.graphics = graphics;
        Gdx.fileIO = fileIO;
        Gdx.audio = audio;
        Gdx.input = input;

        screen = getStartScreen();
        setContentView(renderView);
        
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
    }

    /*
     * Called when game is resumed. The screen is locked, the current screen will be resumed and
     * also the render surface.
     */
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    /*
     * Called when game is paused. The screen is unlocked, the current screen will be paused and
     * also the render surface.
     */
    public void onPause() {
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    /*
     * Returns the Input subsystem.
     */
    public Input getInput() {
        return input;
    }

    /*
     * Returns the FileIO subsystem.
     */
    public FileIO getFileIO() {
        return fileIO;
    }

    /*
     * Returns the Graphics subsystem.
     */
    public Graphics getGraphics() {
        return graphics;
    }

    /*
     * Returns the Audio subsystem.
     */
    public Audio getAudio() {
        return audio;
    }

    /*
     * Sets the current screen.
     */
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        // current screen is paused and disposed
        this.screen.pause();
        this.screen.dispose();
        // input screen is resumed and update and it will be the new current screen.
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    /*
     * Returns the current screen.
     */
    public Screen getCurrentScreen() {
        return screen;
    }
}