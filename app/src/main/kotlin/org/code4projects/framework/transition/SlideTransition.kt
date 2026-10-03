/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.transition

import org.code4projects.framework.Gdx
import org.code4projects.framework.Interpolation
import org.code4projects.framework.Screen

/**
 * Pushes the outgoing screen off one edge while the incoming screen slides in from the opposite
 * one, like moving between pages. Use [Direction.LEFT] to go "forward" and [Direction.RIGHT] to
 * come back.
 *
 * @author Salvatore D'Angelo
 */
class SlideTransition(
    from: Screen,
    to: Screen,
    private val direction: Direction = Direction.LEFT,
    duration: Float = 0.6f,
    interpolation: Interpolation = Interpolation.easeInOut
) : TransitionScreen(from, to, duration, interpolation) {

    /**
     * The direction both screens move in.
     */
    enum class Direction(val dx: Int, val dy: Int) {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1)
    }

    override fun drawTransition(t: Float, deltaTime: Float) {
        val g = Gdx.graphics!!
        val offsetX = direction.dx * g.getWidth() * t
        val offsetY = direction.dy * g.getHeight() * t

        g.save()
        g.translate(offsetX, offsetY)
        from.draw(deltaTime)
        g.restore()

        g.save()
        g.translate(offsetX - direction.dx * g.getWidth(), offsetY - direction.dy * g.getHeight())
        to.draw(deltaTime)
        g.restore()
    }
}
