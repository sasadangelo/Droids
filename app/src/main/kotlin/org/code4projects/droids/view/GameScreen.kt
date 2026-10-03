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
import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Screen

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

        // Gesture tuning for the play field (see the move/drop/hold steps below, which depend on
        // the board's cell size): anything that stays within the tap thresholds (barely moved,
        // released quickly) is treated as a tap-to-rotate instead of a drag.
        private const val TAP_MAX_DISTANCE_PX = 20
        private const val TAP_MAX_DURATION_MS = 250L
        private const val GESTURE_AXIS_LOCK_PX = 24
        private const val GESTURE_AXIS_UNDECIDED = 0
        private const val GESTURE_AXIS_HORIZONTAL = 1
        private const val GESTURE_AXIS_VERTICAL = 2
    }

    // The set of background art cycled through as the level goes up (rather than growing
    // unbounded with level), one hue per level tier.
    private val backgrounds = Assets.gameBackgrounds

    private val states: MutableMap<DroidsWorld.GameState, GameState> = EnumMap(DroidsWorld.GameState::class.java)

    private val layout = GameLayout(Gdx.graphics!!)
    private val pauseButtonBounds = layout.pauseButton

    // Play field gestures, replacing the old left/right/rotate/down buttons: dragging a full
    // cell width moves the piece one column, dragging down two cells soft-drops it, dragging up
    // two cells holds it.
    private val moveStep = layout.cell
    private val dropSwipe = layout.cell * 2
    private val holdSwipe = layout.cell * 2

    private val renderer = DroidsWorldRenderer(layout)
    private val hud = GameHud(layout)
    private val overlay = GameOverlay(layout)

    init {
        Log.i(LOG_TAG, "constructor -- begin")

        states[DroidsWorld.GameState.Paused] = GamePaused()
        states[DroidsWorld.GameState.Ready] = GameReady()
        states[DroidsWorld.GameState.Running] = GameRunning()
        states[DroidsWorld.GameState.GameOver] = GameOver()
        states[DroidsWorld.GameState.Cleared] = GameCleared()
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
        Gdx.graphics!!.drawBackground(background)
        // the HUD panels first, so the Hold/Next shapes the renderer draws sit on top of them
        hud.draw()
        // render the game world: board, blocks, ghost piece, Hold/Next shapes.
        renderer.draw()

        // draw the state specific element
        states[DroidsWorld.getInstance().state]!!.draw()
    }

    /*
     * The screen is paused.
     */
    override fun pause() {
        Log.i(LOG_TAG, "pause -- begin")

        if (DroidsWorld.getInstance().state == DroidsWorld.GameState.Running)
            DroidsWorld.getInstance().state = DroidsWorld.GameState.Paused

        val state = DroidsWorld.getInstance().state
        if (state == DroidsWorld.GameState.GameOver || state == DroidsWorld.GameState.Cleared) {
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
        // already triggered a soft drop or a hold - so a single drag steps the piece at most
        // once per block crossed and triggers each of those at most once, instead of repeating
        // every event.
        private var gestureActive = false
        private var gestureStartX = 0
        private var gestureStartY = 0
        private var gestureStepX = 0
        private var gestureStartTime = 0L
        private var softDropTriggered = false
        private var holdTriggered = false
        private var gestureAxis = GESTURE_AXIS_UNDECIDED

        /*
         * Update the game when it is in running state. The method catches the user's touch
         * gestures on the play field - drag to move, tap to rotate, swipe down to soft-drop,
         * swipe up to hold - and can also pause the game and check for game over.
         */
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GameRunning.update -- begin")
            val len = touchEvents.size
            for (i in 0 until len) {
                val event = touchEvents[i]
                when (event.type) {
                    TouchEvent.TOUCH_DOWN -> {
                        if (layout.board.contains(event.x, event.y)) {
                            gestureActive = true
                            gestureStartX = event.x
                            gestureStartY = event.y
                            gestureStepX = event.x
                            gestureStartTime = SystemClock.uptimeMillis()
                            softDropTriggered = false
                            holdTriggered = false
                            gestureAxis = GESTURE_AXIS_UNDECIDED
                        }
                    }
                    TouchEvent.TOUCH_DRAGGED -> {
                        if (gestureActive) {
                            val deltaX = event.x - gestureStartX
                            val deltaY = event.y - gestureStartY
                            if (gestureAxis == GESTURE_AXIS_UNDECIDED &&
                                maxOf(abs(deltaX), abs(deltaY)) >= GESTURE_AXIS_LOCK_PX
                            ) {
                                gestureAxis = if (abs(deltaX) > abs(deltaY)) {
                                    GESTURE_AXIS_HORIZONTAL
                                } else {
                                    GESTURE_AXIS_VERTICAL
                                }
                            }

                            // Step the falling shape one column per block width crossed, so a
                            // continuous drag slides it left/right across multiple columns.
                            if (gestureAxis != GESTURE_AXIS_VERTICAL) {
                                while (event.x - gestureStepX >= moveStep) {
                                    DroidsWorld.getInstance().fallingShape!!.moveRight()
                                    if (DroidsWorld.getInstance().fallingShape!!.collide())
                                        DroidsWorld.getInstance().fallingShape!!.moveLeft()
                                    gestureStepX += moveStep
                                }
                                while (gestureStepX - event.x >= moveStep) {
                                    DroidsWorld.getInstance().fallingShape!!.moveLeft()
                                    if (DroidsWorld.getInstance().fallingShape!!.collide())
                                        DroidsWorld.getInstance().fallingShape!!.moveRight()
                                    gestureStepX -= moveStep
                                }
                            }
                            // Soft-drop once the finger has dragged down far enough.
                            if (gestureAxis != GESTURE_AXIS_HORIZONTAL &&
                                !softDropTriggered && event.y - gestureStartY >= dropSwipe
                            ) {
                                DroidsWorld.getInstance().fallingShape!!.accelerateFalling()
                                softDropTriggered = true
                            }
                            // Hold once the finger has dragged up far enough.
                            if (gestureAxis != GESTURE_AXIS_HORIZONTAL &&
                                !holdTriggered && gestureStartY - event.y >= holdSwipe
                            ) {
                                DroidsWorld.getInstance().holdFallingShape()
                                holdTriggered = true
                            }
                        }
                    }
                    TouchEvent.TOUCH_UP -> {
                        if (pauseButtonBounds.contains(event.x, event.y)) {
                            Assets.playClick()
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
                Assets.playBitten()
            }
            Assets.playMusic()
        }

        /*
         * Draw the game in running state.
         */
        override fun draw() {
            // nothing on top of the board and HUD while playing
        }
    }

    /*
     * This class represents the game screen in pause state: the pause menu, to resume the game
     * or go back to the start screen (the game stays paused there, ready to be resumed).
     *
     * @author Salvatore D'Angelo
     */
    inner class GamePaused : GameState() {
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GamePaused.update -- begin")
            val menu = overlay.pauseMenu
            for (event in touchEvents) {
                if (event.type != TouchEvent.TOUCH_UP) continue
                if (menu.primaryButton.contains(event.x, event.y)) {
                    Assets.playClick()
                    DroidsWorld.getInstance().state = DroidsWorld.GameState.Running
                    return
                }
                if (menu.secondaryButton.contains(event.x, event.y)) {
                    Assets.playClick()
                    Transitions.leaveGame(this@GameScreen, StartScreen())
                    return
                }
            }
            // pause the music if it is playing.
            Assets.pauseMusic()
        }

        override fun draw() {
            overlay.pauseMenu.draw(Gdx.graphics!!, "PAUSED", emptyList(), "RESUME", "HOME")
        }
    }

    /*
     * This class represents the game screen in ready state: any touch starts the game.
     *
     * @author Salvatore D'Angelo
     */
    inner class GameReady : GameState() {
        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            Log.i(LOG_TAG, "GameReady.update -- begin")
            if (touchEvents.isNotEmpty())
                DroidsWorld.getInstance().state = DroidsWorld.GameState.Running
        }

        override fun draw() {
            overlay.drawReady(Gdx.graphics!!)
        }
    }

    /*
     * Shared by the two end-of-game states (game over, Sprint cleared): play again in the same
     * mode, or go back to the start screen.
     */
    abstract inner class GameEnded : GameState() {
        abstract val menu: GameOverlay.Menu

        override fun update(touchEvents: List<TouchEvent>, deltaTime: Float) {
            for (event in touchEvents) {
                if (event.type != TouchEvent.TOUCH_UP) continue
                if (menu.primaryButton.contains(event.x, event.y)) {
                    Assets.playClick()
                    // Leaving the screen records the score in pause(); staying to play again
                    // has to do it here, before the world is reset.
                    Settings.addScore(DroidsWorld.getInstance().score)
                    Settings.save(Gdx.fileIO!!)
                    DroidsWorld.getInstance().clear()
                    return
                }
                if (menu.secondaryButton.contains(event.x, event.y)) {
                    Assets.playClick()
                    Transitions.leaveGame(this@GameScreen, StartScreen())
                    DroidsWorld.getInstance().clear()
                    return
                }
            }
            // stop the music if it is playing.
            Assets.stopMusic()
        }
    }

    /*
     * This class represents the game screen when the game is over.
     *
     * @author Salvatore D'Angelo
     */
    inner class GameOver : GameEnded() {
        override val menu = overlay.gameOverMenu

        override fun draw() {
            menu.draw(
                Gdx.graphics!!, "GAME OVER", listOf("SCORE" to "${DroidsWorld.getInstance().score}"),
                "PLAY AGAIN", "HOME"
            )
        }
    }

    /*
     * This class represents the game screen when a Sprint run has been won (the line target was
     * reached): it shows the finishing time alongside the score, since that's the number a Sprint
     * run is actually judged on.
     *
     * @author Salvatore D'Angelo
     */
    inner class GameCleared : GameEnded() {
        override val menu = overlay.clearedMenu

        override fun draw() {
            val world = DroidsWorld.getInstance()
            menu.draw(
                Gdx.graphics!!, "CLEARED!",
                listOf("TIME" to formatTime(world.elapsedTime), "SCORE" to "${world.score}"),
                "PLAY AGAIN", "HOME"
            )
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
        Assets.playClick()

        when (DroidsWorld.getInstance().state) {
            DroidsWorld.GameState.Running, DroidsWorld.GameState.Ready ->
                DroidsWorld.getInstance().state = DroidsWorld.GameState.Paused
            DroidsWorld.GameState.Paused ->
                Transitions.leaveGame(this, StartScreen())
            DroidsWorld.GameState.GameOver, DroidsWorld.GameState.Cleared -> {
                Transitions.leaveGame(this, StartScreen())
                DroidsWorld.getInstance().clear()
            }
        }
        return true
    }
}
