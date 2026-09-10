/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import java.io.IOException
import java.io.InputStream

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface

import org.code4projects.framework.Graphics
import org.code4projects.framework.Pixmap
import org.code4projects.framework.TextStyle

/*
 * This class implements the Graphics subsystem for Android. The framebuffer (that in Android is
 * basically a bitmap) will be managed by a Android Canvas object.
 *
 * @author mzechner
 * @author Salvatore D'Angelo
 */
class AndroidGraphics(assets: AssetManager, private val frameBuffer: Bitmap) : Graphics {
    private val fileIO = AndroidFileIO(assets)
    private val canvas = Canvas(frameBuffer)
    private val paint = Paint()
    private val srcRect = Rect()
    private val dstRect = Rect()

    /*
     * Loads a bitmap from filesystem and encapsulate it in a Pixmap object. In Android a bitmap is
     * managed by Android Bitmap class.
     */
    override fun newPixmap(fileName: String, format: Graphics.PixmapFormat): Pixmap {
        val config: Bitmap.Config = when (format) {
            Graphics.PixmapFormat.RGB565 -> Bitmap.Config.RGB_565
            Graphics.PixmapFormat.ARGB4444 -> Bitmap.Config.ARGB_4444
            else -> Bitmap.Config.ARGB_8888
        }

        val options = BitmapFactory.Options()
        options.inPreferredConfig = config

        var input: InputStream? = null
        val bitmap: Bitmap
        try {
            input = fileIO.readAsset(fileName)
            bitmap = BitmapFactory.decodeStream(input)
                ?: throw RuntimeException("Couldn't load bitmap from asset '$fileName'")
        } catch (e: IOException) {
            throw RuntimeException("Couldn't load bitmap from asset '$fileName'")
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        val resultFormat = when (bitmap.config) {
            Bitmap.Config.RGB_565 -> Graphics.PixmapFormat.RGB565
            Bitmap.Config.ARGB_4444 -> Graphics.PixmapFormat.ARGB4444
            else -> Graphics.PixmapFormat.ARGB8888
        }

        return AndroidPixmap(bitmap, resultFormat)
    }

    /*
     * Clears the frame buffer with input color.
     */
    override fun clear(color: Int) {
        canvas.drawRGB((color and 0xff0000) shr 16, (color and 0xff00) shr 8, color and 0xff)
    }

    /*
     * Draws pixel on frame buffer in (x, y) position with color in input.
     */
    override fun drawPixel(x: Int, y: Int, color: Int) {
        paint.color = color
        canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
    }

    /*
     * Draws line on frame buffer from (x1, y1) to (x2, y2) with color in input.
     */
    override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        paint.color = color
        canvas.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), paint)
    }

    /*
     * Draws rectangle on frame buffer with top left corner in (x, y) and size (width, height).
     * The color used will be the one in input.
     */
    override fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawRect(
            x.toFloat(), y.toFloat(), (x + width - 1).toFloat(), (y + width - 1).toFloat(), paint
        )
    }

    /*
     * Draws portion of a bitmap on frame buffer in (x, y) position. The portion of the bitmap
     * is delimited by rectangle defined by top left corner (srcX, srcY) and size (srcWidth,
     * srcHeight).
     */
    override fun drawPixmap(
        pixmap: Pixmap, x: Int, y: Int, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int
    ) {
        srcRect.left = srcX
        srcRect.top = srcY
        srcRect.right = srcX + srcWidth - 1
        srcRect.bottom = srcY + srcHeight - 1

        dstRect.left = x
        dstRect.top = y
        dstRect.right = x + srcWidth - 1
        dstRect.bottom = y + srcHeight - 1

        canvas.drawBitmap((pixmap as AndroidPixmap).bitmap, srcRect, dstRect, null)
    }

    /*
     * Draws bitmap on frame buffer in (x, y) position.
     */
    override fun drawPixmap(pixmap: Pixmap, x: Int, y: Int) {
        canvas.drawBitmap((pixmap as AndroidPixmap).bitmap, x.toFloat(), y.toFloat(), null)
    }

    /*
     * Gets width of the frame buffer.
     */
    override fun getWidth(): Int = frameBuffer.width

    /*
     * Gets height of the frame buffer.
     */
    override fun getHeight(): Int = frameBuffer.height

    /*
     * Draws text in input in (x, y) position and with style in input.
     */
    override fun drawText(text: String, x: Int, y: Int, style: TextStyle) {
        val paint = Paint()
        paint.color = style.color
        paint.textSize = style.textSize.toFloat()

        paint.typeface = when (style.style) {
            TextStyle.Style.BOLD -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            TextStyle.Style.ITALIC -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            else -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        paint.textAlign = when (style.align) {
            TextStyle.Align.RIGHT -> Paint.Align.RIGHT
            TextStyle.Align.CENTER -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    }
}
