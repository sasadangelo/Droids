/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework;

/** <p>
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen
 * and so on.
 * </p>
 * <p>
 * Note that {@link #dispose()} is not called automatically.
 * </p>
 * @see Game
 *
 * @author mzechner
 */
public interface Screen {
    /**
     * Called when the screen should update itself.
     * @param deltaTime The time in seconds since the last update.
     */
    void update(float deltaTime);

    /**
     * Called when the screen should render itself.
     * @param deltaTime The time in seconds since the last render.
     */
    void draw(float deltaTime);

    /**
     * Called when the screen is paused.
     */
    void pause();

    /**
     * Called when the screen is resumed.
     */
    void resume();

    /**
     * Called when the screen is disposed.
     */
    void dispose();
}
