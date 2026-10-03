/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

import org.code4projects.framework.transition.FadeTransition
import org.code4projects.framework.transition.TransitionScreen

/**
 * A screen shown for a fixed time when the game starts (logo, credits...), which then moves on to
 * the screen built by [next]. Tapping skips it, but only after [minDuration] seconds, so a stray
 * touch while the game launches doesn't make it flash by.
 *
 * The framework owns the timing and the hand-off; the game only draws, through [drawSplash],
 * using [elapsed] to animate. The way out defaults to a fade and can be changed via [transition].
 *
 * @author Salvatore D'Angelo
 */
abstract class SplashScreen(
    private val duration: Float,
    private val next: () -> Screen,
    private val minDuration: Float = 0.8f,
    private val transition: (from: Screen, to: Screen) -> Screen = { from, to -> FadeTransition(from, to) }
) : Screen {
    /**
     * Seconds since the splash screen started.
     */
    protected var elapsed = 0f
        private set

    private var finished = false

    override fun update(deltaTime: Float) {
        elapsed += deltaTime.coerceAtMost(TransitionScreen.MAX_FRAME_STEP)
        if (finished) return

        val skipped = elapsed >= minDuration &&
            Gdx.input!!.getTouchEvents().any { it.type == Input.TouchEvent.TOUCH_UP }
        if (skipped || elapsed >= duration) {
            finished = true
            Gdx.game!!.setScreen(transition(this, next()))
        }
    }

    override fun draw(deltaTime: Float) {
        drawSplash(Gdx.graphics!!, deltaTime)
    }

    /**
     * Draw the splash screen; [elapsed] tells how far into it we are.
     */
    protected abstract fun drawSplash(g: Graphics, deltaTime: Float)

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }

    // Back during the splash falls back to the platform default (closing the app).
    override fun backPressed(): Boolean = false
}
