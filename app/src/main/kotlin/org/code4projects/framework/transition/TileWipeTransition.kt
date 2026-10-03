/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.transition

import org.code4projects.framework.Gdx
import org.code4projects.framework.Interpolation
import org.code4projects.framework.Screen

/**
 * Covers the outgoing screen with a grid of tiles appearing one after the other, swaps to the
 * incoming screen once the grid is full, then removes the tiles in the same order to reveal it.
 * [hold] is the fraction of [duration] the screen stays fully covered between the two sweeps.
 *
 * The framework only drives the timing: when each tile starts ([tileDelay], a diagonal sweep by
 * default) and how much it covers at any moment. What a tile looks like is up to the game,
 * through [drawTile].
 *
 * @author Salvatore D'Angelo
 */
abstract class TileWipeTransition(
    from: Screen,
    to: Screen,
    protected val cols: Int,
    protected val rows: Int,
    duration: Float = 1.0f,
    private val spread: Float = 0.6f,
    private val hold: Float = 0f,
    interpolation: Interpolation = Interpolation.linear
) : TransitionScreen(from, to, duration, interpolation) {

    companion object {
        /**
         * How much a tile with the given [delay] (in [0, 1]) covers when the whole sweep is at
         * [sweepProgress] (in [0, 1]). [spread] is the fraction of the sweep over which tile start
         * times are spread out: 0 makes every tile animate together, values close to 1 make each
         * tile animate almost on its own. The last tile still reaches full coverage exactly when
         * the sweep ends.
         */
        fun tileCoverage(sweepProgress: Float, delay: Float, spread: Float): Float {
            if (spread >= 1f) return if (sweepProgress >= delay) 1f else 0f
            return ((sweepProgress - delay * spread) / (1f - spread)).coerceIn(0f, 1f)
        }
    }

    // Computed lazily so subclasses can override tileDelay() using their own fields.
    private val delays: FloatArray by lazy {
        FloatArray(cols * rows) { tileDelay(it % cols, it / cols).coerceIn(0f, 1f) }
    }

    /**
     * When the tile at (col, row) starts animating, in [0, 1]. Defaults to a diagonal sweep from
     * the top-left corner to the bottom-right one.
     */
    protected open fun tileDelay(col: Int, row: Int): Float {
        val last = (cols - 1) + (rows - 1)
        return if (last == 0) 0f else (col + row).toFloat() / last
    }

    /**
     * Draw one tile occupying the cell (x, y, width, height), [coverage] being how much it should
     * cover the screen underneath: 0 = not drawn at all, 1 = fully opaque and full size.
     * [entering] is true while tiles are covering the outgoing screen, false while they are being
     * removed from the incoming one.
     */
    protected abstract fun drawTile(
        x: Int, y: Int, width: Int, height: Int, col: Int, row: Int, coverage: Float, entering: Boolean
    )

    override fun drawTransition(t: Float, deltaTime: Float) {
        val g = Gdx.graphics!!
        // Covering sweep, fully-covered hold, uncovering sweep. The screens swap halfway, while
        // the grid is full, so the swap itself is never visible.
        val sweepTime = (1f - hold.coerceIn(0f, 0.9f)) / 2f
        val entering = t < 0.5f
        val sweep: Float
        if (entering) {
            from.draw(deltaTime)
            sweep = (t / sweepTime).coerceAtMost(1f)
        } else {
            to.draw(deltaTime)
            sweep = ((t - (1f - sweepTime)) / sweepTime).coerceAtLeast(0f)
        }

        val tileWidth = (g.getWidth() + cols - 1) / cols
        val tileHeight = (g.getHeight() + rows - 1) / rows
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val progress = tileCoverage(sweep, delays[row * cols + col], spread)
                val coverage = if (entering) progress else 1f - progress
                if (coverage > 0f) {
                    drawTile(col * tileWidth, row * tileHeight, tileWidth, tileHeight, col, row, coverage, entering)
                }
            }
        }
    }
}
