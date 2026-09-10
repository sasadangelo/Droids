/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen
 * and so on.
 *
 * Note that [dispose] is not called automatically.
 *
 * @see Game
 * @author mzechner
 */
interface Screen {
    /**
     * Called when the screen should update itself.
     * @param deltaTime The time in seconds since the last update.
     */
    fun update(deltaTime: Float)

    /**
     * Called when the screen should render itself.
     * @param deltaTime The time in seconds since the last render.
     */
    fun draw(deltaTime: Float)

    /**
     * Called when the screen is paused.
     */
    fun pause()

    /**
     * Called when the screen is resumed.
     */
    fun resume()

    /**
     * Called when the screen is disposed.
     */
    fun dispose()
}
