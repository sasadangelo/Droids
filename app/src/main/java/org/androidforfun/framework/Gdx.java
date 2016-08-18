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

/**
 * Environment class holding references to the {@link Game}, {@link Graphics}, {@link Audio},
 * {@link FileIO} and {@link Input} instances. The references are held in public static fields which
 * allows static access to all sub systems. Do not use Graphics in a thread that is not the
 * rendering thread.
 * <p>
 * This is normally a design faux pas but in this case is better than the alternatives.
 *
 * @author mzechner
 * @author Salvatore D'Angelo (to fit Gdx library)
 */
public class Gdx {
    public static Game game;
    public static Graphics graphics;
    public static Audio audio;
    public static Input input;
    public static FileIO fileIO;
}
