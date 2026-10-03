/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Build
import android.os.PowerManager
import android.view.Window
import android.view.WindowManager

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Draw behind the camera notch/cutout too: otherwise, with the system bars hidden, the
        // window is shrunk to avoid it and stops matching displayBounds below.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val displayBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Rect(windowManager.currentWindowMetrics.bounds)
        } else {
            Rect(0, 0, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        }

        // Screens are laid out on a fixed area (640x960 in portrait). On a screen taller than that
        // area's aspect ratio - nearly every modern phone - the frame buffer grows taller to match
        // it, so the game fills the whole display instead of being letterboxed; the layout area
        // stays centered in it (see AndroidGraphics). Screens wider than the layout area (tablets)
        // are still pillarboxed.
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val layoutWidth = if (isLandscape) 960 else 640
        val layoutHeight = if (isLandscape) 640 else 960
        val frameBufferWidth = layoutWidth
        val frameBufferHeight = maxOf(
            layoutHeight,
            Math.round(layoutWidth.toFloat() * displayBounds.height() / displayBounds.width())
        )
        val frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565)

        // The frame buffer is drawn letterboxed/pillarboxed (see AndroidFastRenderView.run()) to
        // preserve its aspect ratio instead of stretching it, so touch coordinates - which arrive
        // in full display pixel space - must be mapped through the same offset/scale rather than
        // scaled directly against the raw display size, then shifted into layout coordinates.
        val frameBufferBounds = Rect()
        AndroidFastRenderView.calculateAspectFitRect(displayBounds, frameBufferWidth, frameBufferHeight, frameBufferBounds)

        val scaleX = frameBufferWidth.toFloat() / frameBufferBounds.width()
        val scaleY = frameBufferHeight.toFloat() / frameBufferBounds.height()
        val layoutOffsetY = (frameBufferHeight - layoutHeight) / 2

        renderView = AndroidFastRenderView(this, frameBuffer)
        graphics = AndroidGraphics(assets, frameBuffer, layoutWidth, layoutHeight)
        fileIO = AndroidFileIO(this, assets)
        audio = AndroidAudio(this)
        input = AndroidInput(
            this, renderView, frameBufferBounds.left.toFloat(),
            frameBufferBounds.top + layoutOffsetY / scaleY, scaleX, scaleY
        )

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

    /*
     * Shows a Yes/No confirmation dialog and only runs onConfirm if the user accepts. Kept here,
     * rather than in the droids.view screens, since building a dialog needs an Android Context.
     */
    override fun confirmExit(onConfirm: () -> Unit) {
        // Screens call this from the render thread (e.g. a Quit button in update()), but dialogs
        // can only be created on the UI thread.
        runOnUiThread {
            AlertDialog.Builder(this)
                .setMessage("Exit the game?")
                .setPositiveButton(android.R.string.yes) { _, _ -> onConfirm() }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
    }

    /*
     * Called when the system back button/gesture is triggered. Delegated to the current screen so
     * each one can decide what "back" means for it (pause, navigate away, ask before exiting); if
     * the screen doesn't handle it, fall back to the default platform behavior.
     */
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (!screen.backPressed()) {
            super.onBackPressed()
        }
    }
}
