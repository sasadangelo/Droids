/*
 *  Copyright (C) 2016 Mario Zechner
 *  This file is part of Framework for book Beginning Android Games.
 *
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework;

/**
 * This interface encapsulates communication with the graphics processor.
 * <p>
 * If supported by the backend, this interface lets you query the available display modes (graphics
 * resolution and color depth) and change it.
 *
 * @author mzechner */
public interface Graphics {
    /**
     * Enum describing the bits per pixel and depth buffer precision.
     */
    enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }

    /**
     * Load a bitmap from an image file (i.e. PNG file) with the specified bits per pixels.
     */
    Pixmap newPixmap(String fileName, PixmapFormat format);

    /**
     * Clear the screen with input color.
     * @param color the color used to clear the screen.
     */
    void clear(int color);

    /**
     * Draw a pixel in the position (x, y) with color in input.
     * @param x x position of the pixel.
     * @param y y position of the pixel.
     * @param color the color of the pixel.
     */
    void drawPixel(int x, int y, int color);

    /**
     * Draw a line from point (x1, y1) to point (x2, y2).
     * @param x1 x position of the pixel.
     * @param y1 y position of the pixel.
     * @param x2 x position of the pixel.
     * @param y2 y position of the pixel.
     * @param color the color of the line.
     */
    void drawLine(int x1, int y1, int x2, int y2, int color);

    /**
     * Draw a rectangle with top left corner in (x, y) and having size (width, height).
     * @param x x position of the top left corner.
     * @param y y position of the top left corner.
     * @param width width of the rectangle.
     * @param height width of the rectangle.
     * @param color the color of the rectangle.
     */
    void drawRect(int x, int y, int width, int height, int color);

    /**
     * Draw only the region of a bitmap delimited by rectangle having the top left corner in
     * (srcX, srcY) and size (srcWidth, srcHeight).
     *
     * @param pixmap bitmap to draw.
     * @param x x position where the bitmap must be drawn.
     * @param y y position where the bitmap must be drawn.
     * @param srcX x position of the top left corner of the region of the bitmap to draw.
     * @param srcY y position of the top left corner of the region of the bitmap to draw.
     * @param srcWidth width of the region of the bitmap to draw.
     * @param srcHeight height of the region of the bitmap to draw.
     */
    void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight);

    /**
     * Draw only the region of a bitmap delimited by rectangle having the top left corner in
     * (srcX, srcY) and size (srcWidth, srcHeight).
     *
     * @param pixmap bitmap to draw.
     * @param x x position where the bitmap must be drawn.
     * @param y y position where the bitmap must be drawn.
     */
    void drawPixmap(Pixmap pixmap, int x, int y);

    /**
     * Draw text in (x, y) position with style specified by style.
     *
     * @param text text to draw.
     * @param x x position of the text.
     * @param y y position of the text.
     * @param style style of the text.
     */
    void drawText(String text, int x, int y, TextStyle style);

    /**
     * Return the width of the framebuffer.
     *
     * @return width of the framebuffer.
     */
    int getWidth();

    /**
     * Return the height of the framebuffer.
     *
     * @return height of the framebuffer.
     */
    int getHeight();
}
