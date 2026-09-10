/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.SurfaceView

/*
 * This class is responsible to draw the scene on the screen. It extends the class SurfaceView that
 * provides a drawing surface where user can draw his interface in a separate thread that we call UI
 * Thread.
 *
 * @author mzechner
 */
class AndroidFastRenderView(private val game: AndroidGame, private val framebuffer: Bitmap) :
    SurfaceView(game), Runnable {

    private val surfaceHolder = holder
    private var renderThread: Thread? = null

    @Volatile
    private var running = false

    /*
     * Resume the UI Thread.
     */
    fun resume() {
        running = true
        renderThread = Thread(this)
        renderThread!!.start()
    }

    /*
     * This is the run method executed in the UI Thread. It contains the Game Loop.
     *
     * @author mzechner
     */
    override fun run() {
        val dstRect = Rect()
        var startTime = System.nanoTime()
        // This is the Game Loop
        while (running) {
            // draw only if surface is valid
            if (!surfaceHolder.surface.isValid) continue

            // calculate the time required to update/draw the previous frame.
            val deltaTime = (System.nanoTime() - startTime) / 1000000000.0f
            startTime = System.nanoTime()

            // update and draw the game spending for each phase a deltaTime. This is necessary to
            // avoid to have on high performance hardware a game too quick. The drawing occurs on
            // frame buffer and not drectly on screen. The reason is that if we draw directly on
            // screen the update will not be immediate
            game.getCurrentScreen().update(deltaTime)
            game.getCurrentScreen().draw(deltaTime)

            // Once the frame buffer is ready the surface is locked and it will be drawn on the
            // screen. When completed the lock is release.
            val canvas = surfaceHolder.lockCanvas()
            canvas.getClipBounds(dstRect)
            canvas.drawBitmap(framebuffer, null, dstRect, null)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    /*
     * Pause the UI Thread.
     */
    fun pause() {
        running = false
        while (true) {
            try {
                renderThread!!.join()
                break
            } catch (e: InterruptedException) {
                // retry
            }
        }
    }
}
