/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen
import org.code4projects.framework.impl.FadeTransitionScreen

/*
 * This class represents the highscores screen. The screen show the top five scores achieved by the
 * user.
 *
 * @author Salvatore D'Angelo
 */
class HighscoreScreen : Screen {
    private val backgroundBounds = Rectangle(0, 0, 640, 960)
    private val backButtonBounds = Rectangle(64, 740, 100, 100)

    private val lines = Array(5) { "" }

    /*
     * Initialize the screen with the following scores: 100, 80, 50, 30, 10.
     */
    init {
        for (i in 0 until 5) {
            lines[i] = "" + (i + 1) + ". " + Settings.highscores[i]
        }
    }

    /*
     * Check the user input and if he press the back button go back to the start screen.
     */
    override fun update(deltaTime: Float) {
        val touchEvents = Gdx.input!!.getTouchEvents()
        Gdx.input!!.getKeyEvents()

        val len = touchEvents.size
        for (i in 0 until len) {
            val event = touchEvents[i]
            if (event.type == TouchEvent.TOUCH_UP) {
                if (backButtonBounds.contains(event.x, event.y)) {
                    if (Settings.soundEnabled)
                        Assets.click!!.play(1f)
                    Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
                    return
                }
            }
        }
    }

    /*
     * Draw the highscores screen.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!

        // draw the background.
        g.drawPixmap(Assets.highscoresscreen!!, backgroundBounds.x, backgroundBounds.y)
        // draw the 5 scores.
        var y = 200
        for (i in 0 until 5) {
            drawText(g, lines[i], 40, y)
            y += 100
        }
        // draw the back button.
        g.drawPixmap(
            Assets.buttons!!, backButtonBounds.x, backButtonBounds.y, 100, 100,
            backButtonBounds.width + 1, backButtonBounds.height + 1
        )
    }

    fun drawText(g: Graphics, line: String, x: Int, y: Int) {
        var posX = x
        val len = line.length
        for (i in 0 until len) {
            val character = line[i]

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

            g.drawPixmap(Assets.numbers!!, posX, y, srcX, 0, srcWidth, 64)
            posX += srcWidth
        }
    }

    /*
     * The screen is paused.
     */
    override fun pause() {
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
     * Same as tapping the back button: return to the start screen.
     */
    override fun backPressed(): Boolean {
        if (Settings.soundEnabled)
            Assets.click!!.play(1f)
        Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
        return true
    }
}
