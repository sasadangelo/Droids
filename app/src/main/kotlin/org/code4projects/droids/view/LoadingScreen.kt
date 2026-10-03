/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import android.util.Log

import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Screen
import org.code4projects.framework.transition.FadeTransition

/*
 * This class represents the loading screen. It load in memory all the assets used by the game.
 * Usually games show a progress bar in this screen. To simplify the code and since the assets are
 * loaded very quickly I avoided this complication.
 *
 * @author Salvatore D'Angelo
 */
class LoadingScreen : Screen {
    companion object {
        private const val LOG_TAG = "Droids.LoadingScreen"
    }

    override fun update(deltaTime: Float) {
        Log.i(LOG_TAG, "update -- begin")
        val g: Graphics = Gdx.graphics!!

        Assets.gameBackgrounds = listOf("blue", "purple", "teal", "amber", "crimson", "olive").map {
            g.newPixmap("gamebg_$it.png", Graphics.PixmapFormat.RGB565)
        }
        Assets.hudFont = g.newFont("fonts/LilitaOne-Regular.ttf")
        Assets.playButton = g.newPixmap("button_play.png", Graphics.PixmapFormat.ARGB8888)
        Assets.blueButton = g.newPixmap("button_blue.png", Graphics.PixmapFormat.ARGB8888)
        Assets.iconBack = g.newPixmap("icon_back.png", Graphics.PixmapFormat.ARGB8888)
        Assets.iconTrophy = g.newPixmap("icon_trophy.png", Graphics.PixmapFormat.ARGB8888)
        Assets.iconGear = g.newPixmap("icon_gear.png", Graphics.PixmapFormat.ARGB8888)
        Assets.iconPower = g.newPixmap("icon_power.png", Graphics.PixmapFormat.ARGB8888)
        Assets.splashBackground = g.newPixmap("splash_background.png", Graphics.PixmapFormat.RGB565)
        Assets.splashLogo = g.newPixmap("splash_logo.png", Graphics.PixmapFormat.ARGB8888)
        Assets.wipeBlocks = listOf("pink", "orange", "yellow", "green", "cyan", "blue", "purple").map {
            g.newPixmap("wipe_$it.png", Graphics.PixmapFormat.ARGB8888)
        }

        Assets.playfield = g.newPixmap("playfield.png", Graphics.PixmapFormat.ARGB8888)

        Assets.redblock = g.newPixmap("redblock.png", Graphics.PixmapFormat.ARGB8888)
        Assets.greenblock = g.newPixmap("greenblock.png", Graphics.PixmapFormat.ARGB8888)
        Assets.blueblock = g.newPixmap("blueblock.png", Graphics.PixmapFormat.ARGB8888)
        Assets.cyanblock = g.newPixmap("cyanblock.png", Graphics.PixmapFormat.ARGB8888)
        Assets.yellowblock = g.newPixmap("yellowblock.png", Graphics.PixmapFormat.ARGB8888)
        Assets.magentablock = g.newPixmap("magentablock.png", Graphics.PixmapFormat.ARGB8888)
        Assets.orangeblock = g.newPixmap("orangeblock.png", Graphics.PixmapFormat.ARGB8888)

        // Audio effects
        Assets.click = Gdx.audio!!.newSound("click.ogg")
        Assets.bitten = Gdx.audio!!.newSound("bitten.ogg")

        // Music
        Assets.music = Gdx.audio!!.newMusic("Korobeiniki.ogg")

        Settings.load(Gdx.fileIO!!)
        // This screen draws nothing, so only the second (fade-in) half of the fade is visible.
        Gdx.game!!.setScreen(FadeTransition(this, DroidsSplashScreen(), duration = 1.2f))
    }

    /*
     * Draw nothing.
     */
    override fun draw(deltaTime: Float) {
        Log.i(LOG_TAG, "draw -- begin")
    }

    /*
     * The screen is paused.
     */
    override fun pause() {
    }

    /*
     * The screen is resumed.
     */
    override fun resume() {
    }

    /*
     * The screen is disposed.
     */
    override fun dispose() {
    }

    /*
     * There is nothing to go back to from the loading screen, so let the default behavior
     * (closing the app) happen.
     */
    override fun backPressed(): Boolean = false
}
