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
import org.code4projects.framework.TextStyle
import org.code4projects.framework.impl.FadeTransitionScreen

/*
 * Lets the player choose a game mode before starting a new game: Marathon, Sprint or Endless.
 * There's no baked art for this (unlike the main menu), so the rows are drawn from plain
 * rectangles/text rather than a bitmap, the same way the ghost piece reuses drawRect instead of
 * adding new sprites.
 *
 * @author Salvatore D'Angelo
 */
class ModeSelectScreen : Screen {
    // Subtitles are pre-wrapped into short lines rather than measured/wrapped at draw time -
    // there's no text-measurement primitive in Graphics, so lines are kept short enough by hand
    // to comfortably fit optionBounds' width at optionSubtitleStyle's size.
    private data class ModeOption(val mode: DroidsWorld.GameMode, val title: String, val subtitleLines: List<String>)

    private val options = listOf(
        ModeOption(
            DroidsWorld.GameMode.MARATHON, "Marathon",
            listOf("Speed increases forever -", "survive as long as you can")
        ),
        ModeOption(
            DroidsWorld.GameMode.SPRINT, "Sprint",
            listOf("Clear 40 lines", "as fast as possible")
        ),
        ModeOption(
            DroidsWorld.GameMode.ENDLESS, "Endless",
            listOf("Constant speed, no target -", "just relax and play")
        )
    )

    private val backgroundBounds = Rectangle(0, 0, 640, 960)
    private val backButtonBounds = Rectangle(64, 740, 100, 100)
    private val optionBounds = listOf(
        Rectangle(110, 280, 420, 130),
        Rectangle(110, 430, 420, 130),
        Rectangle(110, 580, 420, 130)
    )

    private val boxColor = 0x99202040L.toInt()
    private val accentColor = 0xff00e5ffL.toInt()

    private val titleStyle = TextStyle().apply {
        color = 0xffffffffL.toInt()
        textSize = 48
        style = TextStyle.Style.BOLD
        align = TextStyle.Align.CENTER
    }

    private val optionTitleStyle = TextStyle().apply {
        color = 0xffffffffL.toInt()
        textSize = 36
        style = TextStyle.Style.BOLD
        align = TextStyle.Align.CENTER
    }

    private val optionSubtitleStyle = TextStyle().apply {
        color = 0xffffffffL.toInt()
        textSize = 20
        style = TextStyle.Style.NORMAL
        align = TextStyle.Align.CENTER
    }

    /*
     * Check the user input: tapping a mode row starts a new game in that mode; tapping back
     * returns to the start screen.
     */
    override fun update(deltaTime: Float) {
        val touchEvents = Gdx.input!!.getTouchEvents()

        val len = touchEvents.size
        for (i in 0 until len) {
            val event = touchEvents[i]
            if (event.type == TouchEvent.TOUCH_UP) {
                if (backButtonBounds.contains(event.x, event.y)) {
                    Assets.playClick()
                    Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
                    return
                }
                for (j in options.indices) {
                    if (optionBounds[j].contains(event.x, event.y)) {
                        Assets.playClick()
                        val world = DroidsWorld.getInstance()
                        world.mode = options[j].mode
                        world.clear()
                        Gdx.game!!.setScreen(FadeTransitionScreen(this, GameScreen()))
                        return
                    }
                }
            }
        }
    }

    /*
     * Draw the mode selection screen.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!

        g.drawPixmap(Assets.startscreen!!, backgroundBounds.x, backgroundBounds.y)
        g.drawText("Select Mode", backgroundBounds.width / 2, 200, titleStyle)

        val currentMode = DroidsWorld.getInstance().mode
        for (i in options.indices) {
            val bounds = optionBounds[i]
            val isSelected = options[i].mode == currentMode
            if (isSelected) {
                g.drawRect(bounds.x - 4, bounds.y - 4, bounds.width + 8, bounds.height + 8, accentColor)
            }
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, boxColor)
            val centerX = bounds.x + bounds.width / 2
            g.drawText(options[i].title, centerX, bounds.y + 45, optionTitleStyle)
            for (lineIndex in options[i].subtitleLines.indices) {
                g.drawText(
                    options[i].subtitleLines[lineIndex], centerX, bounds.y + 82 + lineIndex * 26, optionSubtitleStyle
                )
            }
        }

        // draw the back button.
        g.drawPixmap(
            Assets.buttons!!, backButtonBounds.x, backButtonBounds.y, 100, 100,
            backButtonBounds.width + 1, backButtonBounds.height + 1
        )
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
        Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
        return true
    }
}
