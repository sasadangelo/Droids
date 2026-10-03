/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

import org.code4projects.framework.Graphics
import org.code4projects.framework.Interpolation
import org.code4projects.framework.SplashScreen
import org.code4projects.framework.TextStyle

/*
 * The first thing the player sees: the "DROIDS" block logo pops in over the blue background,
 * a few sparkles twinkle around it and the credits fade in below. It then hands over to the start
 * screen with the block wipe. Timing and tap-to-skip come from the framework's SplashScreen.
 *
 * @author Salvatore D'Angelo
 */
class DroidsSplashScreen : SplashScreen(
    duration = 5.7f,
    next = { StartScreen() },
    transition = { from, to -> BlockWipeTransition(from, to) }
) {
    companion object {
        private const val LOGO_CENTER_X = 320f
        private const val LOGO_CENTER_Y = 400f
        private const val LOGO_START = 0.3f
        private const val LOGO_POP = 1.1f
        private const val SPARKLES_START = 1.1f
        private const val CREDITS_START = 1.6f
        private const val CREDITS_FADE = 0.9f

        // (x, y, size, twinkle speed, phase) of each sparkle around the logo.
        private val SPARKLES = arrayOf(
            floatArrayOf(70f, 300f, 14f, 3.1f, 0.0f),
            floatArrayOf(200f, 290f, 9f, 3.7f, 1.3f),
            floatArrayOf(560f, 310f, 12f, 2.9f, 2.1f),
            floatArrayOf(610f, 430f, 8f, 4.1f, 0.6f),
            floatArrayOf(470f, 495f, 13f, 3.3f, 2.8f),
            floatArrayOf(150f, 500f, 10f, 3.9f, 1.9f),
            floatArrayOf(30f, 420f, 9f, 3.5f, 3.6f),
            floatArrayOf(380f, 285f, 7f, 4.4f, 4.4f),
        )
        private const val SPARKLE_COLOR = 0x7fe3ff
    }

    private val creditsStyle = TextStyle().apply {
        textSize = 24
        align = TextStyle.Align.CENTER
    }

    override fun drawSplash(g: Graphics, deltaTime: Float) {
        g.drawPixmap(Assets.splashBackground!!, 0, 0)
        drawLogo(g)
        drawSparkles(g)
        drawCredits(g)
    }

    /*
     * The logo pops in (scale overshoot + fade), then floats gently up and down.
     */
    private fun drawLogo(g: Graphics) {
        val t = ((elapsed - LOGO_START) / LOGO_POP).coerceIn(0f, 1f)
        if (t <= 0f) return
        val scale = Interpolation.easeOutBack.apply(0.5f, 1f, t)
        val alpha = Interpolation.easeOut.apply(t)
        val bob = if (t < 1f) 0f else sin((elapsed - LOGO_START - LOGO_POP) * 2f) * 6f
        g.drawPixmap(Assets.splashLogo!!, LOGO_CENTER_X, LOGO_CENTER_Y + bob, scale, 0f, alpha)
    }

    /*
     * Small diamonds whose opacity pulses, each at its own speed and phase.
     */
    private fun drawSparkles(g: Graphics) {
        val time = elapsed - SPARKLES_START
        if (time <= 0f) return
        for (s in SPARKLES) {
            val (x, y, size, speed, phase) = s
            val pulse = max(0f, sin(time * speed + phase - PI.toFloat() / 2f))
            val alpha = (255 * pulse * pulse).toInt()
            if (alpha == 0) continue
            val half = size * (0.6f + 0.4f * pulse) / 2f
            g.save()
            g.rotate(45f, x, y)
            g.drawRoundRect(
                (x - half).toInt(), (y - half).toInt(), (half * 2).toInt(), (half * 2).toInt(), half * 0.3f,
                (alpha shl 24) or SPARKLE_COLOR
            )
            g.restore()
        }
    }

    private fun drawCredits(g: Graphics) {
        val t = ((elapsed - CREDITS_START) / CREDITS_FADE).coerceIn(0f, 1f)
        if (t <= 0f) return
        creditsStyle.color = ((255 * t * 0.85f).toInt() shl 24) or 0xffffff
        g.drawText("© 2016-2026 Salvatore D'Angelo", 320, 860, creditsStyle)
        g.drawText("Released under the MIT License", 320, 895, creditsStyle)
    }
}
