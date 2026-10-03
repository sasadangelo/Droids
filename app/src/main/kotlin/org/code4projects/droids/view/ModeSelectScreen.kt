/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen

/*
 * Lets the player choose a game mode before starting a new game: Marathon, Sprint or Endless,
 * one panel each, the last mode played highlighted.
 *
 * @author Salvatore D'Angelo
 */
class ModeSelectScreen : Screen {
    companion object {
        private const val OPTION_WIDTH = 480
        private const val OPTION_HEIGHT = 150
        private const val OPTION_GAP = 28
        private const val SELECTED_BORDER = 0xff5fd0ff.toInt()
    }

    // Subtitles are pre-wrapped into short lines rather than measured/wrapped at draw time -
    // there's no text-measurement primitive in Graphics, so lines are kept short enough by hand
    // to comfortably fit an option's width at subtitleStyle's size.
    private data class ModeOption(val mode: DroidsWorld.GameMode, val title: String, val subtitleLines: List<String>)

    private val options = listOf(
        ModeOption(
            DroidsWorld.GameMode.MARATHON, "MARATHON",
            listOf("Speed increases forever -", "survive as long as you can")
        ),
        ModeOption(
            DroidsWorld.GameMode.SPRINT, "SPRINT",
            listOf("Clear 40 lines", "as fast as possible")
        ),
        ModeOption(
            DroidsWorld.GameMode.ENDLESS, "ENDLESS",
            listOf("Constant speed, no target -", "just relax and play")
        )
    )

    private val page = MenuPage("SELECT MODE")
    private val optionBounds: List<Rectangle>

    private val optionTitleStyle = DroidsUi.textStyle(40, DroidsUi.VALUE_COLOR)
    private val subtitleStyle = DroidsUi.textStyle(24, DroidsUi.TITLE_COLOR)

    init {
        // the three options, centered in the page's content area
        val total = options.size * OPTION_HEIGHT + (options.size - 1) * OPTION_GAP
        val x = (Gdx.graphics!!.getWidth() - OPTION_WIDTH) / 2
        var y = page.contentTop + (page.contentBottom - page.contentTop - total) / 2
        optionBounds = options.map {
            Rectangle(x, y, OPTION_WIDTH, OPTION_HEIGHT).also { y += OPTION_HEIGHT + OPTION_GAP }
        }
    }

    /*
     * Check the user input: tapping a mode starts a new game in that mode; tapping back
     * returns to the start screen.
     */
    override fun update(deltaTime: Float) {
        for (event in Gdx.input!!.getTouchEvents()) {
            if (event.type != TouchEvent.TOUCH_UP) continue
            if (page.backButton.contains(event.x, event.y)) {
                Assets.playClick()
                Transitions.back(this, StartScreen())
                return
            }
            for (i in options.indices) {
                if (optionBounds[i].contains(event.x, event.y)) {
                    Assets.playClick()
                    val world = DroidsWorld.getInstance()
                    world.mode = options[i].mode
                    world.clear()
                    Transitions.play(this, GameScreen())
                    return
                }
            }
        }
    }

    /*
     * Draw the mode selection screen.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!
        page.draw(g)

        val currentMode = DroidsWorld.getInstance().mode
        for (i in options.indices) {
            val bounds = optionBounds[i]
            DroidsUi.drawPanel(g, bounds)
            if (options[i].mode == currentMode) {
                g.drawRoundRectOutline(
                    bounds.x, bounds.y, bounds.width, bounds.height, DroidsUi.PANEL_RADIUS, 5f, SELECTED_BORDER
                )
            }
            val centerX = bounds.x + bounds.width / 2
            g.drawText(options[i].title, centerX, bounds.y + 56, optionTitleStyle)
            for ((line, text) in options[i].subtitleLines.withIndex()) {
                g.drawText(text, centerX, bounds.y + 96 + line * 30, subtitleStyle)
            }
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }

    /*
     * Same as tapping the back button: return to the start screen.
     */
    override fun backPressed(): Boolean {
        Assets.playClick()
        Transitions.back(this, StartScreen())
        return true
    }
}
