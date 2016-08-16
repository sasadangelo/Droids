/*
 *  Copyright (C) 2016 Mario Zechner
 *  This file is part of Framework for book Beginning Android Games.
 *  Modified by Salvatore D'Angelo
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
package org.androidforfun.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;

import org.androidforfun.framework.Graphics;
import org.androidforfun.framework.Pixmap;
import org.androidforfun.framework.TextStyle;

/*
 * This class implements the Graphics subsystem for Android. The framebuffer (that in Android is
 * basically a bitmap) will be managed by a Android Canvas object.
 *
 * @author mzechner
 * @author Salvatore D'Angelo
 */
public class AndroidGraphics implements Graphics {
    AndroidFileIO fileIO;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();

    /*
     * Initializes the Graphics subsystem.
     */
    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        fileIO= new AndroidFileIO(assets);
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
    }

    /*
     * Loads a bitmap from filesystem and encapsulate it in a Pixmap object. In Android a bitmap is
     * managed by Android Bitmap class.
     */
    public Pixmap newPixmap(String fileName, PixmapFormat format) {
        Config config;
        if (format == PixmapFormat.RGB565)
            config = Config.RGB_565;
        else if (format == PixmapFormat.ARGB4444)
            config = Config.ARGB_4444;
        else
            config = Config.ARGB_8888;

        Options options = new Options();
        options.inPreferredConfig = config;

        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = fileIO.readAsset(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (bitmap.getConfig() == Config.RGB_565)
            format = PixmapFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = PixmapFormat.ARGB4444;
        else
            format = PixmapFormat.ARGB8888;

        return new AndroidPixmap(bitmap, format);
    }

    /*
     * Clears the frame buffer with input color.
     */
    public void clear(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }

    /*
     * Draws pixel on frame buffer in (x, y) position with color in input.
     */
    public void drawPixel(int x, int y, int color) {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    /*
     * Draws line on frame buffer from (x1, y1) to (x2, y2) with color in input.
     */
    public void drawLine(int x1, int y1, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    /*
     * Draws rectangle on frame buffer with top left corner in (x, y) and size (width, height).
     * The color used will be the one in input.
     */
    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + width - 1, paint);
    }

    /*
     * Draws portion of a bitmap on frame buffer in (x, y) position. The portion of the bitmap
     * is delimited by rectangle defined by top left corner (srcX, srcY) and size (srcWidth,
     * srcHeight).
     */
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
            int srcWidth, int srcHeight) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth - 1;
        srcRect.bottom = srcY + srcHeight - 1;

        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth - 1;
        dstRect.bottom = y + srcHeight - 1;

        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect,
                null);
    }

    /*
     * Draws bitmap on frame buffer in (x, y) position.
     */
    public void drawPixmap(Pixmap pixmap, int x, int y) {
        canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, x, y, null);
    }

    /*
     * Gets width of the frame buffer.
     */
    public int getWidth() {
        return frameBuffer.getWidth();
    }

    /*
     * Gets height of the frame buffer.
     */
    public int getHeight() {
        return frameBuffer.getHeight();
    }

    /*
     * Draws text in input in (x, y) position and with style in input.
     */
    public void drawText(String text, int x, int y, TextStyle style) {
        Paint paint = new Paint();
        paint.setColor(style.getColor());
        paint.setTextSize(style.getTextSize());

        if (style.getStyle()==TextStyle.Style.BOLD) {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        } else if (style.getStyle()==TextStyle.Style.NORMAL) {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        } else if (style.getStyle()==TextStyle.Style.ITALIC) {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        } else {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }

        Paint.Align align = null;
        if (style.getAlign()==TextStyle.Align.LEFT) {
            paint.setTextAlign(Paint.Align.LEFT);
        } else if (style.getAlign()==TextStyle.Align.RIGHT) {
            paint.setTextAlign(Paint.Align.RIGHT);
        } else if (style.getAlign()==TextStyle.Align.CENTER) {
            paint.setTextAlign(Paint.Align.CENTER);
        } else {
            paint.setTextAlign(Paint.Align.LEFT);
        }
        canvas.drawText(text, x, y, paint);
    }
}
