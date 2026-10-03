/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * Easing curves: map a linear progress in [0, 1] to an eased one, so animations can accelerate,
 * decelerate or overshoot instead of moving at constant speed. Every curve maps 0 to 0 and 1 to
 * 1; inputs outside [0, 1] are clamped.
 *
 * @author Salvatore D'Angelo
 */
fun interface Interpolation {
    fun apply(t: Float): Float

    /**
     * Interpolate between [from] and [to] following this curve.
     */
    fun apply(from: Float, to: Float, t: Float): Float = from + (to - from) * apply(t)

    companion object {
        /** Constant speed. */
        @JvmField val linear = Interpolation { it.coerceIn(0f, 1f) }

        /** Starts slow, speeds up. */
        @JvmField val easeIn = Interpolation { val x = it.coerceIn(0f, 1f); x * x * x }

        /** Starts fast, slows down to a stop. */
        @JvmField val easeOut = Interpolation { val x = 1f - it.coerceIn(0f, 1f); 1f - x * x * x }

        /** Slow at both ends, fast in the middle. The usual choice for screen transitions. */
        @JvmField val easeInOut = Interpolation {
            val x = it.coerceIn(0f, 1f)
            if (x < 0.5f) 4f * x * x * x else 1f - 4f * (1f - x) * (1f - x) * (1f - x)
        }

        /** Overshoots the target a little before settling on it: makes things "pop" in. */
        @JvmField val easeOutBack = Interpolation {
            val x = it.coerceIn(0f, 1f) - 1f
            val overshoot = 1.70158f
            1f + (overshoot + 1f) * x * x * x + overshoot * x * x
        }
    }
}
