/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework;

import org.code4projects.framework.Graphics.PixmapFormat;

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
