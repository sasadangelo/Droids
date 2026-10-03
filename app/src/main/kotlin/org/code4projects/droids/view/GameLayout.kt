/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Graphics
import org.code4projects.framework.Rectangle

/*
 * Where everything goes on the game screen, computed once from the visible area so the game uses
 * the whole height of tall phones: a HUD bar along the top (pause button, Hold, stats, Next) and
 * the board below it, with cells as big as the remaining space allows.
 *
 *     +-------------------------------------------+
 *     | [||] [ HOLD ] [   SCORE   ] [ NEXT ]      |
 *     |              [LEVEL  GOAL ]               |
 *     |      +-----------------------------+      |
 *     |      |                             |      |
 *     |      |        board 10 x 20        |      |
 *     |      |                             |      |
 *     |      +-----------------------------+      |
 *     +-------------------------------------------+
 *
 * @author Salvatore D'Angelo
 */
class GameLayout(g: Graphics) {
    companion object {
        const val BOARD_COLS = 10
        const val BOARD_ROWS = 20

        private const val SIDE_MARGIN = 16
        private const val GAP = 10
        private const val PAUSE_SIZE = 64
        private const val SIDE_PANEL_WIDTH = 120

        // Tall screens get a taller HUD and room at the top for the status bar/camera cutout.
        private const val HUD_HEIGHT_TALL = 150
        private const val HUD_HEIGHT_SHORT = 120
        private const val TOP_MARGIN_TALL = 44
        private const val TOP_MARGIN_SHORT = 12
        private const val TALL_EXTRA_HEIGHT = 120

        private const val BOTTOM_MARGIN = 24
        private const val MAX_CELL = 56

        // playfield.png: 64px cells with a 16px frame margin; the ratio holds at any cell size.
        const val PLAYFIELD_MARGIN_RATIO = 16f / 64f
    }

    val hudTop: Int
    val hudHeight: Int
    val pauseButton: Rectangle
    val holdPanel: Rectangle
    val statsPanel: Rectangle
    val nextPanel: Rectangle

    /** Size in layout units of one board cell. */
    val cell: Int

    /** The board's cells, without its frame. */
    val board: Rectangle

    init {
        val top = g.getVisibleTop()
        val bottom = g.getVisibleBottom()
        val width = g.getWidth()
        val tall = bottom - top - g.getHeight() >= TALL_EXTRA_HEIGHT

        hudTop = top + if (tall) TOP_MARGIN_TALL else TOP_MARGIN_SHORT
        hudHeight = if (tall) HUD_HEIGHT_TALL else HUD_HEIGHT_SHORT

        var x = SIDE_MARGIN
        pauseButton = Rectangle(x, hudTop, PAUSE_SIZE, PAUSE_SIZE)
        x += PAUSE_SIZE + GAP
        holdPanel = Rectangle(x, hudTop, SIDE_PANEL_WIDTH, hudHeight)
        x += SIDE_PANEL_WIDTH + GAP
        nextPanel = Rectangle(width - SIDE_MARGIN - SIDE_PANEL_WIDTH, hudTop, SIDE_PANEL_WIDTH, hudHeight)
        statsPanel = Rectangle(x, hudTop, nextPanel.x - GAP - x, hudHeight)

        // The board takes whatever is left below the HUD, leaving room for its frame.
        val areaTop = hudTop + hudHeight + GAP
        val areaBottom = bottom - BOTTOM_MARGIN
        val byHeight = ((areaBottom - areaTop) / (BOARD_ROWS + 2 * PLAYFIELD_MARGIN_RATIO)).toInt()
        val byWidth = ((width - 2 * SIDE_MARGIN) / (BOARD_COLS + 2 * PLAYFIELD_MARGIN_RATIO)).toInt()
        cell = minOf(byHeight, byWidth, MAX_CELL)

        val boardWidth = cell * BOARD_COLS
        val boardHeight = cell * BOARD_ROWS
        // centered in the leftover space when the cell size is capped
        val boardY = areaTop + (areaBottom - areaTop - boardHeight) / 2
        board = Rectangle((width - boardWidth) / 2, boardY, boardWidth, boardHeight)
    }
}
