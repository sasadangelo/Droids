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

import org.androidforfun.framework.Graphics.PixmapFormat;

/** <p>
 * A Pixmap represents an image in memory. It has a width and height expressed in pixels as well as
 * a {@link PixmapFormat} specifying the number and order of color components per pixel.
 * Coordinates of pixels are specified with respect to the top left corner of the image, with
 * the x-axis pointing to the right and the y-axis pointing downwards.
 * </p>
 *
 * <p>
 * A Pixmap stores its data in native heap memory. It is mandatory to call {@link Pixmap#dispose()}
 * when the pixmap is no longer needed, otherwise memory leaks will result.
 * </p>
 *
 * @author mzechner
 */
public interface Pixmap {
    /**
     * @return The width of the Pixmap in pixels.
     */
    int getWidth();

    /**
     * @return The height of the Pixmap in pixels.
     */
    int getHeight();

    /**
     * @return the {@link PixmapFormat} of this Pixmap.
     */
    PixmapFormat getFormat();

    /**
     * Releases all resources associated with this Pixmap.
     */
    void dispose();

    /**
     * Gets the color of the (x, y) pixel of the image.
     *
     * @param x x coordinate of the pixel to change;
     * @param y y coordinate of the pixel to change;
     * @return the color, encoded as RGBA8888
     */
    int getColor(int x, int y);

    /**
     * Sets the color of the (x, y) pixel of the image.
     *
     * @param x x coordinate of the pixel to change;
     * @param y y coordinate of the pixel to change;
     * @param color the color of the (x, y) pixel, encoded as RGBA8888.
     */
    void setColor(int x, int y, int color);
}
