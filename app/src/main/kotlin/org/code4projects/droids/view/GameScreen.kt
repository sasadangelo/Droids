/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import android.util.Log

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen
import org.code4projects.framework.TextStyle

import java.util.EnumMap

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
    }

    private val states: MutableMap<DroidsWorld.GameState, GameState> = EnumMap(DroidsWorld.GameState::class.java)
    val leftRegion = Rectangle(0, 0, 60, 400)
    val rightRegion = Rectangle(260, 0, 60, 400)
    val workingRegion = Rectangle(60, 20, 200, 400)
    val commandRegion = Rectangle(0, 400, 320, 80)

    private val gameoverScreenBounds = Rectangle(0, 0, 320, 480)
    private val gameScreenBounds = Rectangle(0, 0, 320, 480)
    private val pauseButtonBounds = Rectangle(5, 20, 50, 50)
    private val leftButtonBounds = Rectangle(30, 425, 50, 50)
    private val rightButtonBounds = Rectangle(240, 425, 50, 50)
    private val rotateButtonBounds = Rectangle(100, 425, 50, 50)
    private val downButtonBounds = Rectangle(170, 425, 50, 50)
    private val xButtonBounds = Rectangle(128, 200, 50, 50)
    private val pauseMenuBounds = Rectangle(100, 100, 160, 48)
    private val readyMenuBounds = Rectangle(65, 100, 188, 70)
    private val homeMenuBounds = Rectangle(80, 148, 160, 48)

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
        // draw the background
        Gdx.graphics!!.drawPixmap(Assets.gamescreen!!, gameScreenBounds.x, gameScreenBounds.y)
        // render the game world.
        renderer.draw()
        // draw buttons
        Gdx.graphics!!.drawPixmap(
            Assets.buttons!!, leftButtonBounds.x, leftButtonBounds.y, 50, 50,
            leftButtonBounds.width + 1, leftButtonBounds.height + 1
        ) // left button
        Gdx.graphics!!.drawPixmap(
            Assets.buttons!!, rightButtonBounds.x, rightButtonBounds.y, 0, 50,
            rightButtonBounds.width + 1, rightButtonBounds.height + 1
        ) // right button
        Gdx.graphics!!.drawPixmap(
            Assets.buttons!!, rotateButtonBounds.x, rotateButtonBounds.y, 50, 150,
            rotateButtonBounds.width + 1, rotateButtonBounds.height + 1
        ) // rotate button
        Gdx.graphics!!.drawPixmap(
            Assets.buttons!!, downButtonBounds.x, downButtonBounds.y, 0, 150,
            downButtonBounds.width + 1, downButtonBounds.height + 1
        ) // down button

        // draw the goal, score and level.
        val style = TextStyle()
        style.color = 0xffffffffL.toInt()
        style.textSize = 10
        style.align = TextStyle.Align.CENTER
        Gdx.graphics!!.drawText(
            "" + DroidsWorld.getInstance().level, 30 + leftRegion.x, 165 + leftRegion.y, style
        )
        Gdx.graphics!!.drawText(
            "" + DroidsWorld.getInstance().goal, 30 + leftRegion.x, 265 + leftRegion.y, style
        )
        Gdx.graphics!!.drawText(
            "" + DroidsWorld.getInstance().score, 30 + rightRegion.x, 265 + rightRegion.y, style
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
                posX += 20
                continue
            }

            val srcX: Int
            val srcWidth: Int
            if (character == '.') {
                srcX = 200
                srcWidth = 10
            } else {
                srcX = (character - '0') * 20
                srcWidth = 20
            }

            Gdx.graphics!!.drawPixmap(Assets.numbers!!, posX, y, srcX, 0, srcWidth, 32)
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
        /*
         * Update the game when it is in running state. The method catch the user input and,
         * depending on it will move, rotate or accelerate the falling shape. It can also pause the
         * game and check for game over.
         */
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GameRunning.update -- begin")
            val len = touchEvents.size
            for (i in 0 until len) {
                val event = touchEvents[i]
                when (event.type) {
                    TouchEvent.TOUCH_UP -> {
                        if (pauseButtonBounds.contains(event.x, event.y)) {
                            if (Settings.soundEnabled)
                                Assets.click!!.play(1f)
                            DroidsWorld.getInstance().state = DroidsWorld.GameState.Paused
                            return
                        }
                    }
                    TouchEvent.TOUCH_DOWN -> {
                        // Move falling shape on the left, if possible
                        if (leftButtonBounds.contains(event.x, event.y)) {
                            DroidsWorld.getInstance().fallingShape!!.moveLeft()
                            if (DroidsWorld.getInstance().fallingShape!!.collide())
                                DroidsWorld.getInstance().fallingShape!!.moveRight()
                        }
                        // Move falling shape on the right, if possible
                        if (rightButtonBounds.contains(event.x, event.y)) {
                            DroidsWorld.getInstance().fallingShape!!.moveRight()
                            if (DroidsWorld.getInstance().fallingShape!!.collide())
                                DroidsWorld.getInstance().fallingShape!!.moveLeft()
                        }
                        // Rotate falling shape, if possible
                        if (rotateButtonBounds.contains(event.x, event.y)) {
                            DroidsWorld.getInstance().fallingShape!!.rotate()
                            if (DroidsWorld.getInstance().fallingShape!!.collide())
                                DroidsWorld.getInstance().fallingShape!!.undoRotate()
                        }
                        // Accelerate falling of the falling shape
                        if (downButtonBounds.contains(event.x, event.y)) {
                            DroidsWorld.getInstance().fallingShape!!.accelerateFalling()
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
                Assets.buttons!!, pauseButtonBounds.x, pauseButtonBounds.y, 50, 100,
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
                        Gdx.game!!.setScreen(StartScreen())
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
                Assets.buttons!!, pauseButtonBounds.x, pauseButtonBounds.y, 50, 100,
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
                        Gdx.game!!.setScreen(StartScreen())
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
                Assets.buttons!!, pauseButtonBounds.x, pauseButtonBounds.y, 50, 100,
                pauseButtonBounds.width + 1, pauseButtonBounds.height + 1
            ) // pause button
            // draw game over transparent black background
            g.drawPixmap(Assets.gameoverscreen!!, gameoverScreenBounds.x, gameoverScreenBounds.y)
            // draw the X button
            g.drawPixmap(
                Assets.buttons!!, xButtonBounds.x, xButtonBounds.y, 0, 100,
                xButtonBounds.width + 1, xButtonBounds.height + 1
            ) // down button
            drawText("" + DroidsWorld.getInstance().score, 180, 280)
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
}
