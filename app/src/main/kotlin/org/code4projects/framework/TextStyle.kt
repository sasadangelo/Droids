/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * Represents the style of a text. A text has a size, a color, an alignment and a style (normal,
 * bold, italic).
 *
 * @author Salvatore D'Angelo
 */
class TextStyle {
    /**
     * Text could be aligned on the left, right, centered or justified.
     */
    enum class Align {
        LEFT,
        CENTER,
        RIGHT,
        JUSTIFY
    }

    /**
     * Text could be normal, bold, italic.
     */
    enum class Style {
        NORMAL,
        BOLD,
        ITALIC
    }

    /**
     * The color of the text.
     */
    var color: Int = 0

    /**
     * The size of the text.
     */
    var textSize: Int = 0

    /**
     * The alignment of the text. Possible values are LEFT, CENTER, RIGHT and JUSTIFY.
     */
    var align: Align = Align.LEFT

    /**
     * The style of the text. Possible values are NORMAL, BOLD and ITALIC.
     */
    var style: Style = Style.NORMAL

    /**
     * The font to draw with, loaded by [Graphics.newFont]. When null the platform's default
     * typeface is used, in the given [style]; a custom font is drawn as it is and ignores [style].
     */
    var font: Font? = null

    /**
     * Color of a hard drop shadow drawn [shadowOffset] units below the text; 0 (transparent, the
     * default) means no shadow.
     */
    var shadowColor: Int = 0

    /**
     * How far below the text its shadow is drawn.
     */
    var shadowOffset: Float = 0f
}
