/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import android.os.SystemClock
import android.util.Log

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen
import org.code4projects.framework.TextStyle
import org.code4projects.framework.impl.FadeTransitionScreen

import java.util.EnumMap
import kotlin.math.abs

/*
 * This class represents the game screen. The processing and rendering depends on the game state managed
 * by State pattern. The update and draw method are delegated to:
 *    GamePause.update, GamePause.draw
 *    GameReady.update, GameReady.draw
 *    GameRunning.update, GameRunning.draw
 *    GameOver.update, GameOver.draw
 *
 * depending on the status of the game.
 *
 * @author Salvatore D'Angelo
 */
class GameScreen : Screen {
    companion object {
        private const val LOG_TAG = "Droids.GameScreen"

        // How many levels the game stays on one background before rotating to the next, so
        // progress reads as more than just a faster fall speed.
        private const val LEVELS_PER_BACKGROUND = 3

        // Gesture tuning for the play field, replacing the old left/right/rotate/down buttons:
        // dragging a full block width moves the piece one column, dragging down two block
        // heights soft-drops it, and anything that stays within the tap thresholds (barely
        // moved, released quickly) is treated as a tap-to-rotate instead of a drag.
        private const val MOVE_STEP_PX = DroidsWorldRenderer.BLOCK_WIDTH
        private const val DROP_SWIPE_PX = DroidsWorldRenderer.BLOCK_HEIGHT * 2
        private const val TAP_MAX_DISTANCE_PX = 20
        private const val TAP_MAX_DURATION_MS = 250L
    }

    // The set of background art cycled through as the level goes up (rather than growing
    // unbounded with level). Each is a hue-shifted variant of the original gamescreen art.
    private val backgrounds = arrayOf(
        Assets.gamescreen!!, Assets.gamescreenPurple!!, Assets.gamescreenTeal!!,
        Assets.gamescreenAmber!!, Assets.gamescreenCrimson!!, Assets.gamescreenOlive!!
    )

    private val states: MutableMap<DroidsWorld.GameState, GameState> = EnumMap(DroidsWorld.GameState::class.java)
    val leftRegion = Rectangle(0, 0, 120, 800)
    val rightRegion = Rectangle(520, 0, 120, 800)
    val workingRegion = Rectangle(120, 40, 400, 800)
    val commandRegion = Rectangle(0, 800, 640, 160)

    private val gameoverScreenBounds = Rectangle(0, 0, 640, 960)
    private val gameScreenBounds = Rectangle(0, 0, 640, 960)
    private val pauseButtonBounds = Rectangle(10, 40, 100, 100)
    private val xButtonBounds = Rectangle(256, 400, 100, 100)
    private val pauseMenuBounds = Rectangle(200, 200, 320, 96)
    private val readyMenuBounds = Rectangle(130, 200, 376, 140)
    private val homeMenuBounds = Rectangle(160, 296, 320, 96)

    private val renderer = DroidsWorldRenderer()

    init {
        Log.i(LOG_TAG, "constructor -- begin")

        states[DroidsWorld.GameState.Paused] = GamePaused()
        states[DroidsWorld.GameState.Ready] = GameReady()
        states[DroidsWorld.GameState.Running] = GameRunning()
        states[DroidsWorld.GameState.GameOver] = GameOver()
    }

    /*
     * The update method is delegated to:
     *    GamePause.update
     *    GameReady.update
     *    GameRunning.update
     *    GameOver.update
     *
     * depending on the status of the game.
     */
    override fun update(deltaTime: Float) {
        Log.i(LOG_TAG, "update -- begin")
        val touchEvents = Gdx.input!!.getTouchEvents()
        Gdx.input!!.getKeyEvents()
        states[DroidsWorld.getInstance().state]!!.update(touchEvents, deltaTime)
    }

    /*
     * The draw method is delegated to:
     *    GamePause.draw
     *    GameReady.draw
     *    GameRunning.draw
     *    GameOver.draw
     *
     * depending on the status of the game.
     */
    override fun draw(deltaTime: Float) {
        Log.i(LOG_TAG, "draw -- begin")
        // draw the background, picking the variant for the current level tier
        val background = backgrounds[(DroidsWorld.getInstance().level / LEVELS_PER_BACKGROUND) % backgrounds.size]
        Gdx.graphics!!.drawPixmap(background, gameScreenBounds.x, gameScreenBounds.y)
        // render the game world.
        renderer.draw(this)

        // draw the goal, score and level.
        val style = TextStyle()
        style.color = 0xffffffffL.toInt()
        style.textSize = 20
        style.align = TextStyle.Align.CENTER
        Gdx.graphics!!.drawText(
            "" + DroidsWorld.getInstance().level, 60 + leftRegion.x, 330 + leftRegion.y, style
        )
        Gdx.graphics!!.drawText(
            "" + DroidsWorld.getInstance().goal, 60 + leftRegion.x, 530 + leftRegion.y, style
        )
        Gdx.graphics!!.drawText(
            "" + DroidsWorld.getInstance().score, 60 + rightRegion.x, 530 + rightRegion.y, style
        )

        // draw the state specific element
        states[DroidsWorld.getInstance().state]!!.draw()
    }

    /*
     * Draw text on the screen in the (x, y) position.
     */
    fun drawText(text: String, x: Int, y: Int) {
        Log.i(LOG_TAG, "drawText -- begin")
        var posX = x
        val len = text.length
        for (i in 0 until len) {
            val character = text[i]

            if (character == ' ') {
                posX += 40
                continue
            }

            val srcX: Int
            val srcWidth: Int
            if (character == '.') {
                srcX = 400
                srcWidth = 20
            } else {
                srcX = (character - '0') * 40
                srcWidth = 40
            }

            Gdx.graphics!!.drawPixmap(Assets.numbers!!, posX, y, srcX, 0, srcWidth, 64)
            posX += srcWidth
        }
    }

    /*
     * The screen is paused.
     */
    override fun pause() {
        Log.i(LOG_TAG, "pause -- begin")

        if (DroidsWorld.getInstance().state == DroidsWorld.GameState.Running)
            DroidsWorld.getInstance().state = DroidsWorld.GameState.Paused

        if (DroidsWorld.getInstance().state == DroidsWorld.GameState.GameOver) {
            Settings.addScore(DroidsWorld.getInstance().score)
            Settings.save(Gdx.fileIO!!)
        }
    }

    /*
     * The abstract class representing a generic State. Used to implement the State pattern.
     */
    abstract inner class GameState {
        abstract fun update(touchEvents: List<TouchEvent>, deltaTime: Float)
        abstract fun draw()
    }

    /*
     * This class represents the game screen in running state. It will be responsible to update and
     * draw when the game is running.
     *
     * @author Salvatore D'Angelo
     */
    inner class GameRunning : GameState() {
        // Tracks the touch gesture in progress over the play field: where/when it started, the
        // x position the last horizontal move step was taken from, and whether this gesture
        // already triggered a soft drop - so a single drag steps the piece at most once per
        // block crossed and drops it at most once, instead of repeating every event.
        private var gestureActive = false
        private var gestureStartX = 0
        private var gestureStartY = 0
        private var gestureStepX = 0
        private var gestureStartTime = 0L
        private var softDropTriggered = false

        /*
         * Update the game when it is in running state. The method catches the user's touch
         * gestures on the play field - drag to move, tap to rotate, swipe down to soft-drop -
         * and can also pause the game and check for game over.
         */
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GameRunning.update -- begin")
            val len = touchEvents.size
            for (i in 0 until len) {
                val event = touchEvents[i]
                when (event.type) {
                    TouchEvent.TOUCH_DOWN -> {
                        if (workingRegion.contains(event.x, event.y)) {
                            gestureActive = true
                            gestureStartX = event.x
                            gestureStartY = event.y
                            gestureStepX = event.x
                            gestureStartTime = SystemClock.uptimeMillis()
                            softDropTriggered = false
                        }
                    }
                    TouchEvent.TOUCH_DRAGGED -> {
                        if (gestureActive) {
                            // Step the falling shape one column per block width crossed, so a
                            // continuous drag slides it left/right across multiple columns.
                            while (event.x - gestureStepX >= MOVE_STEP_PX) {
                                DroidsWorld.getInstance().fallingShape!!.moveRight()
                                if (DroidsWorld.getInstance().fallingShape!!.collide())
                                    DroidsWorld.getInstance().fallingShape!!.moveLeft()
                                gestureStepX += MOVE_STEP_PX
                            }
                            while (gestureStepX - event.x >= MOVE_STEP_PX) {
                                DroidsWorld.getInstance().fallingShape!!.moveLeft()
                                if (DroidsWorld.getInstance().fallingShape!!.collide())
                                    DroidsWorld.getInstance().fallingShape!!.moveRight()
                                gestureStepX -= MOVE_STEP_PX
                            }
                            // Soft-drop once the finger has dragged down far enough.
                            if (!softDropTriggered && event.y - gestureStartY >= DROP_SWIPE_PX) {
                                DroidsWorld.getInstance().fallingShape!!.accelerateFalling()
                                softDropTriggered = true
                            }
                        }
                    }
                    TouchEvent.TOUCH_UP -> {
                        if (pauseButtonBounds.contains(event.x, event.y)) {
                            if (Settings.soundEnabled)
                                Assets.click!!.play(1f)
                            DroidsWorld.getInstance().state = DroidsWorld.GameState.Paused
                            return
                        }
                        if (gestureActive) {
                            // A release that barely moved and happened quickly is a tap rather
                            // than the end of a drag/swipe, so rotate instead.
                            val elapsed = SystemClock.uptimeMillis() - gestureStartTime
                            if (abs(event.x - gestureStartX) <= TAP_MAX_DISTANCE_PX &&
                                abs(event.y - gestureStartY) <= TAP_MAX_DISTANCE_PX &&
                                elapsed <= TAP_MAX_DURATION_MS
                            ) {
                                DroidsWorld.getInstance().fallingShape!!.rotateWithWallKick()
                            }
                            gestureActive = false
                        }
                    }
                }
            }

            DroidsWorld.getInstance().update(deltaTime)
            if (DroidsWorld.getInstance().state == DroidsWorld.GameState.GameOver) {
                if (Settings.soundEnabled)
                    Assets.bitten!!.play(1f)
            }
            if (Settings.soundEnabled)
                if (!Assets.music!!.isPlaying()) {
                    Assets.music!!.setLooping(true)
                    Assets.music!!.play()
                }
        }

        /*
         * Draw the game in running state.
         */
        override fun draw() {
            Log.i(LOG_TAG, "GameRunning.draw -- begin")
            Gdx.graphics!!.drawPixmap(
                Assets.buttons!!, pauseButtonBounds.x, pauseButtonBounds.y, 100, 200,
                pauseButtonBounds.width + 1, pauseButtonBounds.height + 1
            ) // pause button
        }
    }

    /*
     * This class represents the game screen in pause state. It will be responsible to update and
     * draw when the game is paused.
     *
     * @author Salvatore D'Angelo
     */
    inner class GamePaused : GameState() {
        /*
         * Update the game when it is in paused state. The method catch the user input and
         * depending on it will resume the game or return to the start screen.
         */
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GamePaused.update -- begin")

            // Check if user asked to resume the game or come back to the start screen.
            val len = touchEvents.size
            for (i in 0 until len) {
                val event = touchEvents[i]
                if (event.type == TouchEvent.TOUCH_UP) {
                    if (pauseMenuBounds.contains(event.x, event.y)) {
                        if (Settings.soundEnabled)
                            Assets.click!!.play(1f)
                        DroidsWorld.getInstance().state = DroidsWorld.GameState.Running
                        return
                    }
                    if (homeMenuBounds.contains(event.x, event.y)) {
                        if (Settings.soundEnabled)
                            Assets.click!!.play(1f)
                        Gdx.game!!.setScreen(FadeTransitionScreen(this@GameScreen, StartScreen()))
                        return
                    }
                }
            }
            // pause the music if it is playing.
            if (Settings.soundEnabled)
                if (Assets.music!!.isPlaying())
                    Assets.music!!.pause()
        }

        /*
         * Draw the game in paused state.
         */
        override fun draw() {
            Log.i(LOG_TAG, "GamePaused.draw -- begin")
            // draw the pause menu
            Gdx.graphics!!.drawPixmap(Assets.pausemenu!!, pauseMenuBounds.x, pauseMenuBounds.y)
        }
    }

    /*
     * This class represents the game screen in ready state. It will be responsible to update and
     * draw when the game is ready.
     *
     * @author Salvatore D'Angelo
     */
    inner class GameReady : GameState() {
        /*
         * Update the game when it is in ready state. The method catch the user input and
         * resume the game.
         */
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GameReady.update -- begin")
            if (touchEvents.isNotEmpty())
                DroidsWorld.getInstance().state = DroidsWorld.GameState.Running
        }

        /*
         * Draw the game in ready state.
         */
        override fun draw() {
            Log.i(LOG_TAG, "GameReady.draw -- begin")
            val g: Graphics = Gdx.graphics!!

            g.drawPixmap(
                Assets.buttons!!, pauseButtonBounds.x, pauseButtonBounds.y, 100, 200,
                pauseButtonBounds.width + 1, pauseButtonBounds.height + 1
            ) // pause button
            // draw the ready menu
            g.drawPixmap(Assets.readymenu!!, readyMenuBounds.x, readyMenuBounds.y)
        }
    }

    /*
     * This class represents the game screen when it is over. It will be responsible to update and
     * draw when the game is over.
     *
     * @author Salvatore D'Angelo
     */
    inner class GameOver : GameState() {
        /*
         * Update the game when it is over. The method catch the user input and return to the
         * start screen.
         */
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GameOver.update -- begin")
            // check if the x button is pressed.
            val len = touchEvents.size
            for (i in 0 until len) {
                val event = touchEvents[i]
                if (event.type == TouchEvent.TOUCH_UP) {
                    if (xButtonBounds.contains(event.x, event.y)) {
                        if (Settings.soundEnabled)
                            Assets.click!!.play(1f)
                        Gdx.game!!.setScreen(FadeTransitionScreen(this@GameScreen, StartScreen()))
                        DroidsWorld.getInstance().clear()
                        return
                    }
                }
            }
            // pause the music if it is playing.
            if (Settings.soundEnabled)
                if (Assets.music!!.isPlaying())
                    Assets.music!!.stop()
        }

        /*
         * Draw the game when it is over.
         */
        override fun draw() {
            Log.i(LOG_TAG, "GameOver.draw -- begin")
            val g: Graphics = Gdx.graphics!!

            // pause button
            Gdx.graphics!!.drawPixmap(
                Assets.buttons!!, pauseButtonBounds.x, pauseButtonBounds.y, 100, 200,
                pauseButtonBounds.width + 1, pauseButtonBounds.height + 1
            ) // pause button
            // draw game over transparent black background
            g.drawPixmap(Assets.gameoverscreen!!, gameoverScreenBounds.x, gameoverScreenBounds.y)
            // draw the X button
            g.drawPixmap(
                Assets.buttons!!, xButtonBounds.x, xButtonBounds.y, 0, 200,
                xButtonBounds.width + 1, xButtonBounds.height + 1
            ) // down button
            drawText("" + DroidsWorld.getInstance().score, 360, 560)
        }
    }

    /*
     * The screen is resumed.
     */
    override fun resume() {
    }

    /*
     * The screen is disposed.
     */
    override fun dispose() {
    }

    /*
     * Back button behavior depends on the game state: pause an in-progress game (same as the
     * pause button) rather than killing the app outright; from the pause menu or game over, go
     * back to the start screen (same as their own "home"/"X" buttons).
     */
    override fun backPressed(): Boolean {
        if (Settings.soundEnabled)
            Assets.click!!.play(1f)

        when (DroidsWorld.getInstance().state) {
            DroidsWorld.GameState.Running, DroidsWorld.GameState.Ready ->
                DroidsWorld.getInstance().state = DroidsWorld.GameState.Paused
            DroidsWorld.GameState.Paused ->
                Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
            DroidsWorld.GameState.GameOver -> {
                Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
                DroidsWorld.getInstance().clear()
            }
        }
        return true
    }
}
