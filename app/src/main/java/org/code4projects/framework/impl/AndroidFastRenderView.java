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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
 * This class is responsible to draw the scene on the screen. It extends the class SurfaceView that
 * provides a drawing surface where user can draw his interface in a separate thread that we call UI
 * Thread.
 *
 * @author mzechner
 */
public class AndroidFastRenderView extends SurfaceView implements Runnable {
    AndroidGame game;
    // The frame buffer
    Bitmap framebuffer;
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;
    
    public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer) {
        super(game);
        this.game = game;
        this.framebuffer = framebuffer;
        this.holder = getHolder();
    }

    /*
     * Resume the UI Thread.
     */
    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();         
    }

    /*
     * This is the run method executed in the UI Thread. It contains the Game Loop.
     *
     * @author mzechner
     */
    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime();
        // This is the Game Loop
        while(running) {
            // draw only if surface is valid
            if(!holder.getSurface().isValid())
                continue;

            // calculate the time required to update/draw the previous frame.
            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            // update and draw the game spending for each phase a deltaTime. This is necessary to
            // avoid to have on high performance hardware a game too quick. The drawing occurs on
            // frame buffer and not drectly on screen. The reason is that if we draw directly on
            // screen the update will not be immediate
            game.getCurrentScreen().update(deltaTime);
            game.getCurrentScreen().draw(deltaTime);
            
            // Once the frame buffer is ready the surface is locked and it will be drawn on the
            // screen. When completed the lock is release.
            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(framebuffer, null, dstRect, null);                           
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /*
     * Pause the UI Thread.
     */
    public void pause() {                        
        running = false;                        
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }        
}