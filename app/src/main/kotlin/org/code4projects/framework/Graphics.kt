/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * This interface encapsulates communication with the graphics processor.
 *
 * If supported by the backend, this interface lets you query the available display modes (graphics
 * resolution and color depth) and change it.
 *
 * @author mzechner
 */
interface Graphics {
    /**
     * Enum describing the bits per pixel and depth buffer precision.
     */
    enum class PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }

    /**
     * Load a bitmap from an image file (i.e. PNG file) with the specified bits per pixels.
     */
    fun newPixmap(fileName: String, format: PixmapFormat): Pixmap

    /**
     * Clear the screen with input color.
     * @param color the color used to clear the screen.
     */
    fun clear(color: Int)

    /**
     * Draw a pixel in the position (x, y) with color in input.
     */
    fun drawPixel(x: Int, y: Int, color: Int)

    /**
     * Draw a line from point (x1, y1) to point (x2, y2).
     */
    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Int)

    /**
     * Draw a rectangle with top left corner in (x, y) and having size (width, height).
     */
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int)

    /**
     * Draw only the region of a bitmap delimited by rectangle having the top left corner in
     * (srcX, srcY) and size (srcWidth, srcHeight).
     */
    fun drawPixmap(pixmap: Pixmap, x: Int, y: Int, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int)

    /**
     * Draw only the region of a bitmap delimited by rectangle having the top left corner in
     * (srcX, srcY) and size (srcWidth, srcHeight).
     */
    fun drawPixmap(pixmap: Pixmap, x: Int, y: Int)

    /**
     * Draw text in (x, y) position with style specified by style.
     */
    fun drawText(text: String, x: Int, y: Int, style: TextStyle)

    /**
     * Return the width of the framebuffer.
     */
    fun getWidth(): Int

    /**
     * Return the height of the framebuffer.
     */
    fun getHeight(): Int
}
