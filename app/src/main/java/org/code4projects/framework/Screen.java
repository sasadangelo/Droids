/*
 *  Copyright (C) 2016 Mario Zechner
 *  This file is part of Framework for book Beginning Android Games.
 *
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework;

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
