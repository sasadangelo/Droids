/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.framework.Music
import org.code4projects.framework.Pixmap
import org.code4projects.framework.Sound

/*
 * This class contains the global references to all the assets used in DroidsWorld game.
 *
 * @author Salvatore D'Angelo
 */
object Assets {
    // the logo asset
    @JvmField var logo: Pixmap? = null

    // the screen used in DroidsWorld game
    @JvmField var gamescreen: Pixmap? = null
    @JvmField var startscreen: Pixmap? = null
    @JvmField var highscoresscreen: Pixmap? = null
    @JvmField var gameoverscreen: Pixmap? = null

    // the menu used in DroidsWorld game
    @JvmField var mainmenu: Pixmap? = null
    @JvmField var pausemenu: Pixmap? = null
    @JvmField var readymenu: Pixmap? = null

    // these are the colored block to draw the DroidsWorld shape. Each shape is composed by 4 blocks
    // of same colors. Each shape has a different color.
    @JvmField var redblock: Pixmap? = null
    @JvmField var greenblock: Pixmap? = null
    @JvmField var blueblock: Pixmap? = null
    @JvmField var cyanblock: Pixmap? = null
    @JvmField var yellowblock: Pixmap? = null
    @JvmField var magentablock: Pixmap? = null
    @JvmField var orangeblock: Pixmap? = null

    // these are the colored block to draw the DroidsWorld next shape.
    @JvmField var smallredblock: Pixmap? = null
    @JvmField var smallgreenblock: Pixmap? = null
    @JvmField var smallblueblock: Pixmap? = null
    @JvmField var smallcyanblock: Pixmap? = null
    @JvmField var smallyellowblock: Pixmap? = null
    @JvmField var smallmagentablock: Pixmap? = null
    @JvmField var smallorangeblock: Pixmap? = null

    // buttons and numbers
    @JvmField var buttons: Pixmap? = null
    @JvmField var numbers: Pixmap? = null

    // sounds
    @JvmField var click: Sound? = null
    @JvmField var bitten: Sound? = null

    // music
    @JvmField var music: Music? = null

    @JvmStatic
    fun getBlockByColor(color: Int): Pixmap? = when (color) {
        0xffffff00L.toInt() -> yellowblock
        0xffb2ffffL.toInt() -> cyanblock
        0xff0000ffL.toInt() -> blueblock
        0xffff7f00L.toInt() -> orangeblock
        0xff00ff00L.toInt() -> greenblock
        0xffff00ffL.toInt() -> magentablock
        0xffff0000L.toInt() -> redblock
        else -> redblock
    }

    @JvmStatic
    fun getSmallBlockByColor(color: Int): Pixmap? = when (color) {
        0xffffff00L.toInt() -> smallyellowblock
        0xffb2ffffL.toInt() -> smallcyanblock
        0xff0000ffL.toInt() -> smallblueblock
        0xffff7f00L.toInt() -> smallorangeblock
        0xff00ff00L.toInt() -> smallgreenblock
        0xffff00ffL.toInt() -> smallmagentablock
        0xffff0000L.toInt() -> smallredblock
        else -> smallredblock
    }
}
