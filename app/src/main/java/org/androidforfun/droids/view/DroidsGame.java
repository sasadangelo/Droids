/*
 *  Copyright (C) 2016 Salvatore D'Angelo
 *  This file is part of Droids project.
 *  This file derives from the Mr Nom project developed by Mario Zechner for the Beginning Android
 *  Games book (chapter 6).
 *
 *  Droids is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Droids is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.droids.view;

import org.androidforfun.framework.Screen;
import org.androidforfun.framework.impl.AndroidGame;

/*
 * This class represents the main activity of the Droids game.
 *
 * @author Salvatore D'Angelo
 */
public class DroidsGame extends AndroidGame {
    /*
     * The first screen of the Droids game is the LoadingScreen used to load all the assets in memory.
     * Usually these screen have a progress bar that represents the percentace of work done. In our
     * case we avoided this complication because the assets are loaded very quickly.
     */
    public Screen getStartScreen() {
        return new LoadingScreen();
    }
}