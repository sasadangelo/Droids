/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import org.code4projects.framework.Gdx
import org.code4projects.framework.Screen

/*
 * A Screen decorator that cross-fades through black between two screens instead of cutting
 * instantly. During the first half of the duration it fades the outgoing screen to black; during
 * the second half it fades the incoming screen in from black. Neither screen is updated while the
 * transition plays - only their draw() output is reused - so input handling only resumes once the
 * transition is over and the game hands control to the incoming screen for real.
 *
 * @author Salvatore D'Angelo
 */
class FadeTransitionScreen(
    private val from: Screen,
    private val to: Screen,
    private val duration: Float = 0.3f
) : Screen {
    private var elapsed = 0f

    /*
     * Advance the fade timer. Once both halves of the transition have played, hand control over
     * to the incoming screen through the normal Game.setScreen() path, which is what actually
     * resumes it and makes it the screen future update()/draw() calls go to.
     */
    override fun update(deltaTime: Float) {
        elapsed += deltaTime
        if (elapsed >= duration * 2) {
            Gdx.game!!.setScreen(to)
        }
    }

    /*
     * Draw the outgoing screen fading to black, then the incoming screen fading in from black.
     */
    override fun draw(deltaTime: Float) {
        val g = Gdx.graphics!!
        val alpha: Float
        if (elapsed < duration) {
            from.draw(deltaTime)
            alpha = elapsed / duration
        } else {
            to.draw(deltaTime)
            alpha = 1f - (elapsed - duration) / duration
        }

        val alphaChannel = (255 * alpha.coerceIn(0f, 1f)).toInt()
        if (alphaChannel > 0) {
            g.drawRect(0, 0, g.getWidth(), g.getHeight(), alphaChannel shl 24)
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}
