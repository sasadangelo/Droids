/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

import org.code4projects.framework.Graphics.PixmapFormat

/**
 * A Pixmap represents an image in memory. It has a width and height expressed in pixels as well as
 * a [PixmapFormat] specifying the number and order of color components per pixel.
 * Coordinates of pixels are specified with respect to the top left corner of the image, with
 * the x-axis pointing to the right and the y-axis pointing downwards.
 *
 * A Pixmap stores its data in native heap memory. It is mandatory to call [dispose]
 * when the pixmap is no longer needed, otherwise memory leaks will result.
 *
 * @author mzechner
 */
interface Pixmap {
    /**
     * @return The width of the Pixmap in pixels.
     */
    fun getWidth(): Int

    /**
     * @return The height of the Pixmap in pixels.
     */
    fun getHeight(): Int

    /**
     * @return the [PixmapFormat] of this Pixmap.
     */
    fun getFormat(): PixmapFormat

    /**
     * Releases all resources associated with this Pixmap.
     */
    fun dispose()

    /**
     * Gets the color of the (x, y) pixel of the image.
     *
     * @return the color, encoded as RGBA8888
     */
    fun getColor(x: Int, y: Int): Int

    /**
     * Sets the color of the (x, y) pixel of the image.
     */
    fun setColor(x: Int, y: Int, color: Int)
}
