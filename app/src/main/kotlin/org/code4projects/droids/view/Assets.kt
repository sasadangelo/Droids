/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.Settings
import org.code4projects.framework.Font
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

    // splash screen: blue background and the "DROIDS" block logo
    @JvmField var splashBackground: Pixmap? = null
    @JvmField var splashLogo: Pixmap? = null

    // one glossy block per color, used by BlockWipeTransition
    @JvmField var wipeBlocks: List<Pixmap> = emptyList()

    // game screen backgrounds, one hue per level tier, cycled through as the level goes up so
    // the game's look visibly changes over time, not just the fall speed.
    @JvmField var gameBackgrounds: List<Pixmap> = emptyList()

    // the font of the in-game HUD
    @JvmField var hudFont: Font? = null

    @JvmField var startscreen: Pixmap? = null
    @JvmField var highscoresscreen: Pixmap? = null
    @JvmField var gameoverscreen: Pixmap? = null

    // the menu used in DroidsWorld game
    @JvmField var mainmenu: Pixmap? = null
    @JvmField var pausemenu: Pixmap? = null
    @JvmField var readymenu: Pixmap? = null

    // the 10x20 board drawn over the game screen background, with a margin for its frame
    @JvmField var playfield: Pixmap? = null

    // these are the colored block to draw the DroidsWorld shape. Each shape is composed by 4 blocks
    // of same colors. Each shape has a different color.
    @JvmField var redblock: Pixmap? = null
    @JvmField var greenblock: Pixmap? = null
    @JvmField var blueblock: Pixmap? = null
    @JvmField var cyanblock: Pixmap? = null
    @JvmField var yellowblock: Pixmap? = null
    @JvmField var magentablock: Pixmap? = null
    @JvmField var orangeblock: Pixmap? = null

    // buttons
    @JvmField var buttons: Pixmap? = null

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

    /*
     * The main hue of the glossy block sprite for a block color, for things drawn in code in the
     * same color as the blocks (e.g. the ghost piece outline). Matches assetstemplate/artkit.py.
     */
    @JvmStatic
    fun getBlockTint(color: Int): Int = when (color) {
        0xffffff00L.toInt() -> 0xffffd000L.toInt()
        0xffb2ffffL.toInt() -> 0xff14b8f0L.toInt()
        0xff0000ffL.toInt() -> 0xff5c7cffL.toInt()
        0xffff7f00L.toInt() -> 0xffff8a00L.toInt()
        0xff00ff00L.toInt() -> 0xff3cd62aL.toInt()
        0xffff00ffL.toInt() -> 0xffc055f0L.toInt()
        else -> 0xffff4f6eL.toInt()
    }

    // Centralizes SFX/music gating and volume so call sites don't each have to know about
    // Settings.sfxEnabled/musicEnabled and their volume levels.
    @JvmStatic
    fun playClick() {
        if (Settings.sfxEnabled) click?.play(Settings.sfxVolume)
    }

    @JvmStatic
    fun playBitten() {
        if (Settings.sfxEnabled) bitten?.play(Settings.sfxVolume)
    }

    @JvmStatic
    fun playMusic() {
        val m = music ?: return
        if (!Settings.musicEnabled) return
        m.setVolume(Settings.musicVolume)
        if (!m.isPlaying()) {
            m.setLooping(true)
            m.play()
        }
    }

    @JvmStatic
    fun pauseMusic() {
        val m = music ?: return
        if (m.isPlaying()) m.pause()
    }

    @JvmStatic
    fun stopMusic() {
        val m = music ?: return
        if (m.isPlaying()) m.stop()
    }

    // Applies the current music volume/mute state to the already-loaded music track - called
    // when the volume/toggle changes on the settings screen while a game may already be running.
    @JvmStatic
    fun updateMusicVolume() {
        music?.setVolume(if (Settings.musicEnabled) Settings.musicVolume else 0f)
    }
}
