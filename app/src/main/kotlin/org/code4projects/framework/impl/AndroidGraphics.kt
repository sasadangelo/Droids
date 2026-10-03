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
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface

import org.code4projects.framework.Font
import org.code4projects.framework.Graphics
import org.code4projects.framework.Pixmap
import org.code4projects.framework.TextStyle

/*
 * This class implements the Graphics subsystem for Android. The framebuffer (that in Android is
 * basically a bitmap) will be managed by a Android Canvas object.
 *
 * Screens are laid out on a fixed layoutWidth x layoutHeight area. The frame buffer is as wide as
 * that area but may be taller, to match the screen's aspect ratio: the layout area is then centered
 * vertically in it (the canvas is translated once, here) and the extra rows above and below are
 * reachable through negative y / y beyond layoutHeight, see getVisibleTop()/getVisibleBottom().
 *
 * @author mzechner
 * @author Salvatore D'Angelo
 */
class AndroidGraphics(
    private val assets: AssetManager,
    private val frameBuffer: Bitmap,
    private val layoutWidth: Int,
    private val layoutHeight: Int
) : Graphics {
    private val layoutOffsetY = (frameBuffer.height - layoutHeight) / 2
    private val canvas = Canvas(frameBuffer).apply { translate(0f, layoutOffsetY.toFloat()) }
    private val paint = Paint()
    private val srcRect = Rect()
    private val dstRect = Rect()
    private val matrix = Matrix()
    // Filtered so scaled/rotated pixmaps don't look jagged; only used by the animated drawPixmap.
    private val pixmapPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val roundRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

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
            input = assets.open(fileName)
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
     * Loads a font from the assets.
     */
    override fun newFont(fileName: String): Font =
        AndroidFont(Typeface.createFromAsset(assets, fileName))

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
            x.toFloat(), y.toFloat(), (x + width - 1).toFloat(), (y + height - 1).toFloat(), paint
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
     * Same as the other region drawPixmap(), but scales the source region to (dstWidth,
     * dstHeight) instead of drawing it at its native size.
     */
    override fun drawPixmap(
        pixmap: Pixmap, x: Int, y: Int, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int,
        dstWidth: Int, dstHeight: Int
    ) {
        srcRect.left = srcX
        srcRect.top = srcY
        srcRect.right = srcX + srcWidth - 1
        srcRect.bottom = srcY + srcHeight - 1

        dstRect.left = x
        dstRect.top = y
        dstRect.right = x + dstWidth - 1
        dstRect.bottom = y + dstHeight - 1

        canvas.drawBitmap((pixmap as AndroidPixmap).bitmap, srcRect, dstRect, null)
    }

    /*
     * Draws bitmap on frame buffer in (x, y) position.
     */
    override fun drawPixmap(pixmap: Pixmap, x: Int, y: Int) {
        canvas.drawBitmap((pixmap as AndroidPixmap).bitmap, x.toFloat(), y.toFloat(), null)
    }

    /*
     * Draws the whole bitmap centered on (centerX, centerY), scaled, rotated around its center
     * and blended with the given opacity. Nothing is drawn when it would be invisible anyway.
     */
    override fun drawPixmap(
        pixmap: Pixmap, centerX: Float, centerY: Float, scale: Float, rotation: Float, alpha: Float
    ) {
        val opacity = (255 * alpha.coerceIn(0f, 1f)).toInt()
        if (opacity == 0 || scale <= 0f) return

        val bitmap = (pixmap as AndroidPixmap).bitmap
        matrix.reset()
        matrix.postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
        matrix.postScale(scale, scale)
        matrix.postRotate(rotation)
        matrix.postTranslate(centerX, centerY)
        pixmapPaint.alpha = opacity
        canvas.drawBitmap(bitmap, matrix, pixmapPaint)
    }

    /*
     * Draws the pixmap centered vertically on the layout area, then stretches its first and last
     * rows over whatever visible area it leaves uncovered above and below.
     */
    override fun drawBackground(pixmap: Pixmap) {
        val width = pixmap.getWidth()
        val height = pixmap.getHeight()
        val y = (layoutHeight - height) / 2
        drawPixmap(pixmap, 0, y)

        // Not the region drawPixmap(): it treats rect edges as inclusive, which collapses a 1px
        // tall source row to nothing.
        val bitmap = (pixmap as AndroidPixmap).bitmap
        val top = getVisibleTop()
        if (y > top) {
            srcRect.set(0, 0, width, 1)
            dstRect.set(0, top, layoutWidth, y)
            canvas.drawBitmap(bitmap, srcRect, dstRect, null)
        }
        val bottom = getVisibleBottom()
        if (y + height < bottom) {
            srcRect.set(0, height - 1, width, height)
            dstRect.set(0, y + height, layoutWidth, bottom)
            canvas.drawBitmap(bitmap, srcRect, dstRect, null)
        }
    }

    /*
     * Draws an anti-aliased filled rectangle with rounded corners.
     */
    override fun drawRoundRect(x: Int, y: Int, width: Int, height: Int, radius: Float, color: Int) {
        roundRectPaint.color = color
        canvas.drawRoundRect(
            x.toFloat(), y.toFloat(), (x + width).toFloat(), (y + height).toFloat(), radius, radius,
            roundRectPaint
        )
    }

    /*
     * Draws an anti-aliased rounded rectangle outline. The stroke is centered on the path, so the
     * path is inset by half the stroke width to keep the outline within the given bounds.
     */
    override fun drawRoundRectOutline(
        x: Int, y: Int, width: Int, height: Int, radius: Float, strokeWidth: Float, color: Int
    ) {
        val half = strokeWidth / 2f
        outlinePaint.color = color
        outlinePaint.strokeWidth = strokeWidth
        canvas.drawRoundRect(
            x + half, y + half, x + width - half, y + height - half, radius - half, radius - half,
            outlinePaint
        )
    }

    override fun save() {
        canvas.save()
    }

    override fun restore() {
        canvas.restore()
    }

    override fun translate(dx: Float, dy: Float) {
        canvas.translate(dx, dy)
    }

    override fun scale(sx: Float, sy: Float, px: Float, py: Float) {
        canvas.scale(sx, sy, px, py)
    }

    override fun rotate(degrees: Float, px: Float, py: Float) {
        canvas.rotate(degrees, px, py)
    }

    /*
     * Gets width of the layout area.
     */
    override fun getWidth(): Int = layoutWidth

    /*
     * Gets height of the layout area.
     */
    override fun getHeight(): Int = layoutHeight

    override fun getVisibleTop(): Int = -layoutOffsetY

    override fun getVisibleBottom(): Int = frameBuffer.height - layoutOffsetY

    /*
     * Draws text in input in (x, y) position and with style in input.
     */
    override fun drawText(text: String, x: Int, y: Int, style: TextStyle) {
        val paint = Paint()
        paint.color = style.color
        paint.textSize = style.textSize.toFloat()

        paint.isAntiAlias = true
        paint.typeface = (style.font as AndroidFont?)?.typeface ?: when (style.style) {
            TextStyle.Style.BOLD -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            TextStyle.Style.ITALIC -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            else -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        paint.textAlign = when (style.align) {
            TextStyle.Align.RIGHT -> Paint.Align.RIGHT
            TextStyle.Align.CENTER -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }
        if (style.shadowColor != 0) {
            val color = paint.color
            paint.color = style.shadowColor
            canvas.drawText(text, x.toFloat(), y + style.shadowOffset, paint)
            paint.color = color
        }
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    }
}
