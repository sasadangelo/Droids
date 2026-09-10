/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Screen
import org.code4projects.framework.impl.AndroidGame

/*
 * This class represents the main activity of the Droids game.
 *
 * @author Salvatore D'Angelo
 */
class DroidsGame : AndroidGame() {
    /*
     * The first screen of the Droids game is the LoadingScreen used to load all the assets in memory.
     * Usually these screen have a progress bar that represents the percentace of work done. In our
     * case we avoided this complication because the assets are loaded very quickly.
     */
    override fun getStartScreen(): Screen = LoadingScreen()
}
