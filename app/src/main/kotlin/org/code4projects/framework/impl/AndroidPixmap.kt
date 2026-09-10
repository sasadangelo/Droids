/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.graphics.Bitmap

import org.code4projects.framework.Graphics
import org.code4projects.framework.Pixmap

/*
 * This class represents a bitmap. On Android a bitmap is managed by a Bitmap class.
 * Each bitmap contains also the format on how its colors are stored (PixmapFormat).
 *
 * @author mzechner
 */
class AndroidPixmap(val bitmap: Bitmap, private val format: Graphics.PixmapFormat) : Pixmap {

    /*
     * Gets the width of the bitmap.
     */
    override fun getWidth(): Int = bitmap.width

    /*
     * Gets the height of the bitmap.
     */
    override fun getHeight(): Int = bitmap.height

    /*
     * Gets the format of the bitmap. Possible values are: ARGB8888, ARGB4444, RGB565.
     */
    override fun getFormat(): Graphics.PixmapFormat = format

    /*
     * Disposes a bitmap.
     */
    override fun dispose() {
        bitmap.recycle()
    }

    /*
     * Gets the color of the pixel in (x, y) position.
     */
    override fun getColor(x: Int, y: Int): Int = bitmap.getPixel(x, y)

    /*
     * Sets the color of the pixel in (x, y) position.
     */
    override fun setColor(x: Int, y: Int, color: Int) {
        bitmap.setPixel(x, y, color)
    }
}
