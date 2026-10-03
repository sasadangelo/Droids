/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Rectangle

/*
 * The common frame of Droids' menu pages (mode select, high scores, settings): the blue
 * background, a back button in the top-left corner and the page title next to it, laid out from
 * the visible area. [contentTop]/[contentBottom] delimit the space left for the page's own content.
 *
 * @author Salvatore D'Angelo
 */
class MenuPage(private val title: String) {
    companion object {
        private const val SIDE_MARGIN = 16
        private const val BACK_SIZE = 72
        private const val ICON_SIZE = 72
        private const val GAP = 24

        // Same top margins as the game HUD: room for the status bar/camera on tall screens.
        private const val TOP_MARGIN_TALL = 44
        private const val TOP_MARGIN_SHORT = 16
        private const val TALL_EXTRA_HEIGHT = 120
        private const val BOTTOM_MARGIN = 32
    }

    val backButton: Rectangle
    val contentTop: Int
    val contentBottom: Int

    private val titleStyle = DroidsUi.textStyle(48, DroidsUi.VALUE_COLOR)

    init {
        val g = Gdx.graphics!!
        val top = g.getVisibleTop()
        val bottom = g.getVisibleBottom()
        val tall = bottom - top - g.getHeight() >= TALL_EXTRA_HEIGHT
        val headerTop = top + if (tall) TOP_MARGIN_TALL else TOP_MARGIN_SHORT

        backButton = Rectangle(SIDE_MARGIN, headerTop, BACK_SIZE, BACK_SIZE)
        contentTop = headerTop + BACK_SIZE + GAP
        contentBottom = bottom - BOTTOM_MARGIN
    }

    fun draw(g: Graphics) {
        g.drawBackground(Assets.splashBackground!!)
        DroidsUi.drawPanel(g, backButton)
        g.drawPixmap(
            Assets.iconBack!!, backButton.x + (backButton.width - ICON_SIZE) / 2,
            backButton.y + (backButton.height - ICON_SIZE) / 2
        )
        // centered on the screen, vertically aligned with the back button
        val baseline = backButton.y + backButton.height / 2 + (titleStyle.textSize * 0.35f).toInt()
        g.drawText(title, g.getWidth() / 2, baseline, titleStyle)
    }
}
