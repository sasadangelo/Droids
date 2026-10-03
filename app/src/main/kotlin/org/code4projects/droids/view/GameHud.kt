/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Rectangle

/*
 * The in-game HUD along the top of the game screen: the pause button, the Hold and Next panels
 * (their shapes are drawn by DroidsWorldRenderer) and the stats panel in between - the score on
 * top, level and goal below it (in Sprint mode: time and lines left, since the level never
 * changes there).
 *
 * @author Salvatore D'Angelo
 */
class GameHud(private val layout: GameLayout) {
    private val titleStyle = DroidsUi.textStyle(24, DroidsUi.TITLE_COLOR)
    private val smallTitleStyle = DroidsUi.textStyle(20, DroidsUi.TITLE_COLOR)
    private val bigValueStyle = DroidsUi.textStyle(40, DroidsUi.VALUE_COLOR)
    private val valueStyle = DroidsUi.textStyle(28, DroidsUi.VALUE_COLOR)

    fun draw() {
        val g = Gdx.graphics!!
        drawPauseButton(g, layout.pauseButton)

        drawPanel(g, layout.holdPanel, "HOLD")
        drawPanel(g, layout.nextPanel, "NEXT")
        drawStats(g, layout.statsPanel)
    }

    private fun drawPanel(g: Graphics, panel: Rectangle, title: String?) {
        DroidsUi.drawPanel(g, panel)
        if (title != null) {
            g.drawText(title, panel.x + panel.width / 2, panel.y + 28, titleStyle)
        }
    }

    /*
     * Score across the top half, two smaller stats side by side in the bottom half. Positions are
     * proportional to the panel height so the short (2:3 screens) HUD works too.
     */
    private fun drawStats(g: Graphics, panel: Rectangle) {
        drawPanel(g, panel, null)
        val world = DroidsWorld.getInstance()
        val h = panel.height
        val centerX = panel.x + panel.width / 2

        g.drawText("SCORE", centerX, panel.y + h * 20 / 100, smallTitleStyle)
        g.drawText("${world.score}", centerX, panel.y + h * 48 / 100, bigValueStyle)

        val (leftTitle, leftValue, rightTitle, rightValue) = if (world.mode == DroidsWorld.GameMode.SPRINT) {
            val linesLeft = (DroidsWorld.SPRINT_TARGET_LINES - world.linesCleared).coerceAtLeast(0)
            listOf("TIME", formatTime(world.elapsedTime), "LINES", "$linesLeft")
        } else {
            listOf("LEVEL", "${world.level}", "GOAL", "${world.goal}")
        }
        val leftX = panel.x + panel.width / 4
        val rightX = panel.x + panel.width * 3 / 4
        g.drawText(leftTitle, leftX, panel.y + h * 68 / 100, smallTitleStyle)
        g.drawText(leftValue, leftX, panel.y + h * 90 / 100, valueStyle)
        g.drawText(rightTitle, rightX, panel.y + h * 68 / 100, smallTitleStyle)
        g.drawText(rightValue, rightX, panel.y + h * 90 / 100, valueStyle)
    }

    /*
     * A square panel with the two bars of a pause icon.
     */
    private fun drawPauseButton(g: Graphics, button: Rectangle) {
        drawPanel(g, button, null)
        val barWidth = button.width / 7
        val barHeight = button.height * 2 / 5
        val barY = button.y + (button.height - barHeight) / 2
        val centerX = button.x + button.width / 2
        g.drawRoundRect(centerX - barWidth * 3 / 2, barY, barWidth, barHeight, 3f, DroidsUi.TITLE_COLOR)
        g.drawRoundRect(centerX + barWidth / 2, barY, barWidth, barHeight, 3f, DroidsUi.TITLE_COLOR)
    }
}

/*
 * Formats a duration in seconds as "m:ss", used by Sprint mode's timer.
 */
fun formatTime(seconds: Float): String {
    val totalSeconds = seconds.toInt()
    return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}
