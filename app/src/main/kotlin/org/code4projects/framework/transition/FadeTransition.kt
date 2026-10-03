/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.transition

import org.code4projects.framework.Gdx
import org.code4projects.framework.Interpolation
import org.code4projects.framework.Screen

/**
 * Cross-fades through a solid [color] (black by default): the first half fades the outgoing
 * screen out, the second half fades the incoming screen in.
 *
 * @author Salvatore D'Angelo
 */
class FadeTransition(
    from: Screen,
    to: Screen,
    duration: Float = 0.8f,
    private val color: Int = 0x000000,
    interpolation: Interpolation = Interpolation.linear
) : TransitionScreen(from, to, duration, interpolation) {

    override fun drawTransition(t: Float, deltaTime: Float) {
        val g = Gdx.graphics!!
        val coverage = if (t < 0.5f) {
            from.draw(deltaTime)
            t * 2f
        } else {
            to.draw(deltaTime)
            (1f - t) * 2f
        }

        val alpha = (255 * coverage.coerceIn(0f, 1f)).toInt()
        if (alpha > 0) {
            g.drawRect(0, 0, g.getWidth(), g.getHeight(), (alpha shl 24) or (color and 0xffffff))
        }
    }
}
