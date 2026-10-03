/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import android.os.SystemClock

import kotlin.math.sin

import org.code4projects.framework.Graphics
import org.code4projects.framework.Rectangle

/*
 * The panels drawn over the board for the non-running game states - "Ready?", the pause menu,
 * game over and Sprint cleared - in the same style as the rest of the UI: a navy panel centered
 * on the board with a title, optional stats and two glossy buttons, the screen dimmed behind it.
 *
 * @author Salvatore D'Angelo
 */
class GameOverlay(layout: GameLayout) {
    companion object {
        private const val DIM_COLOR = 0x99000000.toInt()
        private const val PANEL_WIDTH = 440
        private const val PADDING = 30
        private const val TITLE_HEIGHT = 70
        private const val STAT_HEIGHT = 96
        private const val BUTTON_WIDTH = 360
        private const val BUTTON_GAP = 18
        private const val READY_WIDTH = 420
        private const val READY_HEIGHT = 190
    }

    /*
     * A menu panel with [statCount] stats (label + value) and two buttons, centered on the board.
     */
    inner class Menu(statCount: Int) {
        val panel: Rectangle
        val primaryButton: Rectangle
        val secondaryButton: Rectangle
        val statsTop: Int

        init {
            val buttonHeight = DroidsUi.buttonHeight(BUTTON_WIDTH)
            val height = PADDING + TITLE_HEIGHT + statCount * STAT_HEIGHT + 2 * buttonHeight + BUTTON_GAP + PADDING
            panel = Rectangle(centerX - PANEL_WIDTH / 2, centerY - height / 2, PANEL_WIDTH, height)
            statsTop = panel.y + PADDING + TITLE_HEIGHT
            val buttonX = centerX - BUTTON_WIDTH / 2
            primaryButton = Rectangle(buttonX, statsTop + statCount * STAT_HEIGHT, BUTTON_WIDTH, buttonHeight)
            secondaryButton = Rectangle(buttonX, primaryButton.y + buttonHeight + BUTTON_GAP, BUTTON_WIDTH, buttonHeight)
        }

        /*
         * [stats] are (label, value) pairs, one per stat slot.
         */
        fun draw(g: Graphics, title: String, stats: List<Pair<String, String>>, primary: String, secondary: String) {
            dimScreen(g)
            DroidsUi.drawPanel(g, panel)
            g.drawText(title, centerX, panel.y + PADDING + 50, titleStyle)
            for ((i, stat) in stats.withIndex()) {
                val top = statsTop + i * STAT_HEIGHT
                g.drawText(stat.first, centerX, top + 26, statLabelStyle)
                g.drawText(stat.second, centerX, top + 74, statValueStyle)
            }
            DroidsUi.drawButton(g, primaryButton, Assets.playButton!!, primary, buttonStyle)
            DroidsUi.drawButton(g, secondaryButton, Assets.blueButton!!, secondary, buttonStyle)
        }
    }

    private val centerX = layout.board.x + layout.board.width / 2
    private val centerY = layout.board.y + layout.board.height / 2

    private val titleStyle = DroidsUi.textStyle(52, DroidsUi.VALUE_COLOR)
    private val statLabelStyle = DroidsUi.textStyle(24, DroidsUi.TITLE_COLOR)
    private val statValueStyle = DroidsUi.textStyle(48, DroidsUi.VALUE_COLOR)
    private val buttonStyle = DroidsUi.textStyle(40, DroidsUi.VALUE_COLOR)
    private val readyStyle = DroidsUi.textStyle(64, DroidsUi.VALUE_COLOR)
    private val tapStyle = DroidsUi.textStyle(30, DroidsUi.TITLE_COLOR)

    val pauseMenu = Menu(0)
    val gameOverMenu = Menu(1)
    val clearedMenu = Menu(2)

    private val readyPanel = Rectangle(centerX - READY_WIDTH / 2, centerY - READY_HEIGHT / 2, READY_WIDTH, READY_HEIGHT)

    /*
     * "Ready?" with a pulsing "tap to start" below it. The board stays visible, not dimmed, so
     * the player can see the first piece.
     */
    fun drawReady(g: Graphics) {
        DroidsUi.drawPanel(g, readyPanel)
        g.drawText("READY?", centerX, readyPanel.y + 92, readyStyle)
        val pulse = 0.55f + 0.45f * sin(SystemClock.uptimeMillis() / 250f)
        tapStyle.color = ((255 * pulse).toInt() shl 24) or (DroidsUi.TITLE_COLOR and 0xffffff)
        g.drawText("TAP TO START", centerX, readyPanel.y + 150, tapStyle)
    }

    private fun dimScreen(g: Graphics) {
        val top = g.getVisibleTop()
        g.drawRect(0, top, g.getWidth(), g.getVisibleBottom() - top, DIM_COLOR)
    }
}
