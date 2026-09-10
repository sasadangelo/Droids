/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PowerManager
import android.view.Window
import android.view.WindowManager

import org.code4projects.framework.Audio
import org.code4projects.framework.FileIO
import org.code4projects.framework.Game
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input
import org.code4projects.framework.Screen

/*
 * On Android a Game interface is implemented by an Activity. It will manage the game lifecycle and
 * also it will manage the subsystem of the game library (Graphics, FileIO, Audio and Input).
 * It will create the frame buffer.
 *
 * @author mzechner
 */
abstract class AndroidGame : Activity(), Game {
    private lateinit var renderView: AndroidFastRenderView
    private lateinit var graphics: Graphics
    private lateinit var audio: Audio
    private lateinit var input: Input
    private lateinit var fileIO: FileIO
    private lateinit var screen: Screen
    private lateinit var wakeLock: PowerManager.WakeLock

    /*
     * Initialize the Game subsystems and frame buffer. It will create also the first screen of
     * the game: the start screen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val frameBufferWidth = if (isLandscape) 480 else 320
        val frameBufferHeight = if (isLandscape) 320 else 480
        val frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565)

        val scaleX = frameBufferWidth.toFloat() / windowManager.defaultDisplay.width
        val scaleY = frameBufferHeight.toFloat() / windowManager.defaultDisplay.height

        renderView = AndroidFastRenderView(this, frameBuffer)
        graphics = AndroidGraphics(assets, frameBuffer)
        fileIO = AndroidFileIO(assets)
        audio = AndroidAudio(this)
        input = AndroidInput(this, renderView, scaleX, scaleY)

        Gdx.game = this
        Gdx.graphics = graphics
        Gdx.fileIO = fileIO
        Gdx.audio = audio
        Gdx.input = input

        screen = getStartScreen()
        setContentView(renderView)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame")
    }

    /*
     * Called when game is resumed. The screen is locked, the current screen will be resumed and
     * also the render surface.
     */
    override fun onResume() {
        super.onResume()
        wakeLock.acquire()
        screen.resume()
        renderView.resume()
    }

    /*
     * Called when game is paused. The screen is unlocked, the current screen will be paused and
     * also the render surface.
     */
    override fun onPause() {
        super.onPause()
        wakeLock.release()
        renderView.pause()
        screen.pause()

        if (isFinishing) {
            screen.dispose()
        }
    }

    /*
     * Returns the Input subsystem.
     */
    override fun getInput(): Input = input

    /*
     * Returns the FileIO subsystem.
     */
    override fun getFileIO(): FileIO = fileIO

    /*
     * Returns the Graphics subsystem.
     */
    override fun getGraphics(): Graphics = graphics

    /*
     * Returns the Audio subsystem.
     */
    override fun getAudio(): Audio = audio

    /*
     * Sets the current screen.
     */
    override fun setScreen(screen: Screen) {
        // current screen is paused and disposed
        this.screen.pause()
        this.screen.dispose()
        // input screen is resumed and update and it will be the new current screen.
        screen.resume()
        screen.update(0f)
        this.screen = screen
    }

    /*
     * Returns the current screen.
     */
    override fun getCurrentScreen(): Screen = screen
}
