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
import org.code4projects.framework.TextStyle

/*
 * This class represents the highscores screen: the top five scores achieved on this device, one
 * row each, the podium places in gold, silver and bronze.
 *
 * @author Salvatore D'Angelo
 */
class HighscoreScreen : Screen {
    companion object {
        private const val ROW_WIDTH = 480
        private const val ROW_HEIGHT = 96
        private const val ROW_GAP = 18
        private const val RANK_COLORS_PODIUM = 3
        private val RANK_COLORS = intArrayOf(0xffffd000.toInt(), 0xffd8e2f0.toInt(), 0xffff9a4a.toInt())
    }

    private val page = MenuPage("HIGH SCORES")
    private val rows: List<Rectangle>

    private val rankStyles = List(Settings.highscores.size) { i ->
        DroidsUi.textStyle(48, if (i < RANK_COLORS_PODIUM) RANK_COLORS[i] else DroidsUi.TITLE_COLOR)
    }
    private val scoreStyle = DroidsUi.textStyle(48, DroidsUi.VALUE_COLOR).apply { align = TextStyle.Align.RIGHT }

    init {
        val count = Settings.highscores.size
        val total = count * ROW_HEIGHT + (count - 1) * ROW_GAP
        val x = (Gdx.graphics!!.getWidth() - ROW_WIDTH) / 2
        var y = page.contentTop + (page.contentBottom - page.contentTop - total) / 2
        rows = List(count) { Rectangle(x, y, ROW_WIDTH, ROW_HEIGHT).also { y += ROW_HEIGHT + ROW_GAP } }
    }

    /*
     * Check the user input and if he press the back button go back to the start screen.
     */
    override fun update(deltaTime: Float) {
        for (event in Gdx.input!!.getTouchEvents()) {
            if (event.type == TouchEvent.TOUCH_UP && page.backButton.contains(event.x, event.y)) {
                Assets.playClick()
                Transitions.back(this, StartScreen())
                return
            }
        }
    }

    /*
     * Draw the highscores screen.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!
        page.draw(g)

        for ((i, row) in rows.withIndex()) {
            DroidsUi.drawPanel(g, row)
            val baseline = row.y + row.height / 2 + 17
            g.drawText("${i + 1}", row.x + 56, baseline, rankStyles[i])
            g.drawText("${Settings.highscores[i]}", row.x + row.width - 32, baseline, scoreStyle)
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
        Assets.playClick()
        Transitions.back(this, StartScreen())
        return true
    }
}
