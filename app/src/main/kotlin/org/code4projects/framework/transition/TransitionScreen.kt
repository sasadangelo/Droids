/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.transition

import org.code4projects.framework.Gdx
import org.code4projects.framework.Interpolation
import org.code4projects.framework.Screen

/**
 * Base class for an animated switch between two screens. It is itself a [Screen]: the game sets
 * it as the current screen, it plays for [duration] seconds and then hands control over to [to]
 * through the normal [org.code4projects.framework.Game.setScreen] path.
 *
 * Neither screen is updated while the transition plays - only their draw() output is reused - so
 * input handling only resumes once the incoming screen is the current one for real. Subclasses
 * only decide how to combine the two screens' drawings at a given eased progress.
 *
 * @author Salvatore D'Angelo
 */
abstract class TransitionScreen(
    protected val from: Screen,
    protected val to: Screen,
    private val duration: Float,
    private val interpolation: Interpolation = Interpolation.linear
) : Screen {
    companion object {
        /**
         * Longest step an animation advances by in one frame. The frame right after a slow one
         * (asset loading, building a heavy screen) carries that whole delay in its deltaTime;
         * without a cap the animation would jump straight to its end instead of playing.
         */
        const val MAX_FRAME_STEP = 1f / 20f
    }

    private var elapsed = 0f

    /**
     * Raw progress of the transition in [0, 1], before easing.
     */
    val progress: Float
        get() = if (duration <= 0f) 1f else (elapsed / duration).coerceIn(0f, 1f)

    override fun update(deltaTime: Float) {
        elapsed += deltaTime.coerceAtMost(MAX_FRAME_STEP)
        if (elapsed >= duration) {
            Gdx.game!!.setScreen(to)
        }
    }

    override fun draw(deltaTime: Float) {
        drawTransition(interpolation.apply(progress), deltaTime)
    }

    /**
     * Draw the transition at eased progress [t]: 0 shows only [from], 1 only [to].
     */
    protected abstract fun drawTransition(t: Float, deltaTime: Float)

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }

    // Swallow back presses while a transition is in flight rather than letting either the
    // outgoing or incoming screen react mid-animation.
    override fun backPressed(): Boolean = true
}
