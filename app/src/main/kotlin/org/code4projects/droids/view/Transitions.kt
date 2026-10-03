/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Gdx
import org.code4projects.framework.Screen
import org.code4projects.framework.transition.FadeTransition
import org.code4projects.framework.transition.SlideTransition

/*
 * The single place that decides which transition Droids uses for each kind of navigation, so
 * screens say *where* they go and the look stays consistent:
 *
 *     forward   - into a menu page (settings, highscores, mode select): slide left
 *     back      - back out of a menu page: slide right
 *     play      - into a game: block wipe
 *     leaveGame - out of a game, back home: fade through black
 *
 * @author Salvatore D'Angelo
 */
object Transitions {
    fun forward(from: Screen, to: Screen) = go(SlideTransition(from, to, SlideTransition.Direction.LEFT))

    fun back(from: Screen, to: Screen) = go(SlideTransition(from, to, SlideTransition.Direction.RIGHT))

    fun play(from: Screen, to: Screen) = go(BlockWipeTransition(from, to))

    fun leaveGame(from: Screen, to: Screen) = go(FadeTransition(from, to))

    private fun go(transition: Screen) {
        Gdx.game!!.setScreen(transition)
    }
}
