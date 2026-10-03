/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Graphics
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

    fun drawPanel(g: Graphics, panel: Rectangle) {
        g.drawRoundRect(panel.x, panel.y, panel.width, panel.height, PANEL_RADIUS, PANEL_FILL)
        g.drawRoundRectOutline(
            panel.x, panel.y, panel.width, panel.height, PANEL_RADIUS, PANEL_BORDER_WIDTH, PANEL_BORDER
        )
    }
}
