/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl;

import android.graphics.Bitmap;

import org.code4projects.framework.Graphics.PixmapFormat;
import org.code4projects.framework.Pixmap;

/*
 * This class represents a bitmap. On Android a bitmap is managed by a Bitmap class.
 * Each bitmap contains also the format on how its colors are stored (PixmapFormat).
 *
 * @author mzechner
 */
public class AndroidPixmap implements Pixmap {
    Bitmap bitmap;
    PixmapFormat format;

    /*
     * Initializes the bitmap.
     */
    public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    /*
     * Gets the width of the bitmap.
     */
    public int getWidth() {
        return bitmap.getWidth();
    }

    /*
     * Gets the height of the bitmap.
     */
    public int getHeight() {
        return bitmap.getHeight();
    }

    /*
     * Gets the format of the bitmap. Possible values are: ARGB8888, ARGB4444, RGB565.
     */
    public PixmapFormat getFormat() {
        return format;
    }

    /*
     * Disposes a bitmap.
     */
    public void dispose() {
        bitmap.recycle();
    }

    /*
     * Gets the color of the pixel in (x, y) position.
     */
    public int getColor(int x, int y) {
        return bitmap.getPixel(x, y);
    }

    /*
     * Sets the color of the pixel in (x, y) position.
     */
    public void setColor(int x, int y, int color) {
        bitmap.setPixel(x, y, color);
    }
}
