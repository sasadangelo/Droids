/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import kotlin.random.Random

import org.code4projects.framework.Gdx
import org.code4projects.framework.Interpolation
import org.code4projects.framework.Screen
import org.code4projects.framework.transition.TileWipeTransition

/*
 * The Droids-specific screen transition: the screen fills up with glossy colored blocks popping
 * in along a diagonal sweep, then they pop out the same way revealing the next screen. The
 * framework's TileWipeTransition drives the timing; this class only decides each block's color,
 * a little jitter in the sweep and how a block looks while it pops.
 *
 * @author Salvatore D'Angelo
 */
class BlockWipeTransition(from: Screen, to: Screen) :
    TileWipeTransition(from, to, COLS, ROWS, DURATION, SPREAD, HOLD) {

    companion object {
        // 640x960 frame buffer split in 80x80 cells, the size of the wipe_*.png blocks.
        private const val COLS = 8
        private const val ROWS = 12
        private const val DURATION = 4.4f
        private const val SPREAD = 0.65f
        // Fraction of DURATION the screen stays full of blocks before they start popping out.
        private const val HOLD = 0.12f
        private const val JITTER = 0.12f

        // Same navy as the darkest part of the splash/icon background, drawn behind each block
        // so the gaps between rounded blocks don't show the screen swap happening underneath.
        private const val GROUT_COLOR = 0x0b1a66
    }

    // A different color layout and sweep jitter every time, but fixed for the whole transition.
    private val random = Random(System.nanoTime())
    private val colors = IntArray(COLS * ROWS) { random.nextInt(Assets.wipeBlocks.size) }
    private val jitter = FloatArray(COLS * ROWS) { (random.nextFloat() - 0.5f) * 2f * JITTER }

    override fun tileDelay(col: Int, row: Int): Float =
        super.tileDelay(col, row) + jitter[row * COLS + col]

    override fun drawTile(
        x: Int, y: Int, width: Int, height: Int, col: Int, row: Int, coverage: Float, entering: Boolean
    ) {
        val g = Gdx.graphics!!
        val grout = (255 * coverage * coverage).toInt()
        g.drawRect(x, y, width, height, (grout shl 24) or GROUT_COLOR)

        // Blocks overshoot a little as they pop in, and shrink away with a quarter turn.
        val scale = if (entering) Interpolation.easeOutBack.apply(coverage) else Interpolation.easeOut.apply(coverage)
        val rotation = if (entering) 0f else (1f - coverage) * 90f
        g.drawPixmap(
            Assets.wipeBlocks[colors[row * COLS + col]], x + width / 2f, y + height / 2f, scale, rotation, 1f
        )
    }
}
