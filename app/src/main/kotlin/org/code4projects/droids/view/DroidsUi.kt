/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Graphics
import org.code4projects.framework.Pixmap
import org.code4projects.framework.Rectangle
import org.code4projects.framework.TextStyle

/*
 * The shared look of Droids' UI drawn in code - navy panels with a blue border, cyan titles and
 * white values in the game font with a drop shadow - so the HUD, the home screen and the menus
 * stay consistent.
 *
 * @author Salvatore D'Angelo
 */
object DroidsUi {
    const val PANEL_FILL = 0xd90b1650.toInt()
    const val PANEL_BORDER = 0xff3d5ce0.toInt()
    const val PANEL_RADIUS = 14f
    const val PANEL_BORDER_WIDTH = 3f
    const val TITLE_COLOR = 0xff5fd0ff.toInt()
    const val VALUE_COLOR = 0xffffffff.toInt()
    const val SHADOW_COLOR = 0xff050a30.toInt()

    /*
     * A centered text style in the game font, with a drop shadow proportional to its size.
     */
    fun textStyle(size: Int, textColor: Int) = TextStyle().apply {
        font = Assets.hudFont
        textSize = size
        color = textColor
        align = TextStyle.Align.CENTER
        shadowColor = SHADOW_COLOR
        shadowOffset = size / 10f
    }

    // Native size of the glossy button images (button_play.png, button_blue.png).
    private const val BUTTON_WIDTH = 420f

    /*
     * Draws a glossy button image scaled to the rectangle's width (keeping its proportions,
     * centered on the rectangle) with a centered label.
     */
    fun drawButton(g: Graphics, bounds: Rectangle, button: Pixmap, label: String, style: TextStyle) {
        val centerX = bounds.x + bounds.width / 2f
        val centerY = bounds.y + bounds.height / 2f
        g.drawPixmap(button, centerX, centerY, bounds.width / BUTTON_WIDTH, 0f, 1f)
        g.drawText(label, centerX.toInt(), (centerY + style.textSize * 0.32f).toInt(), style)
    }

    /*
     * Height of a button drawn [width] wide by drawButton(), from the images' proportions.
     */
    fun buttonHeight(width: Int): Int = width * 136 / 420

    fun drawPanel(g: Graphics, panel: Rectangle) {
        g.drawRoundRect(panel.x, panel.y, panel.width, panel.height, PANEL_RADIUS, PANEL_FILL)
        g.drawRoundRectOutline(
            panel.x, panel.y, panel.width, panel.height, PANEL_RADIUS, PANEL_BORDER_WIDTH, PANEL_BORDER
        )
    }
}
