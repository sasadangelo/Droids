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

        Assets.gamescreen = g.newPixmap("gamescreen.png", Graphics.PixmapFormat.RGB565)
        Assets.logo = g.newPixmap("logo.png", Graphics.PixmapFormat.RGB565)

        // Screens
        Assets.startscreen = g.newPixmap("startscreen.png", Graphics.PixmapFormat.RGB565)
        Assets.highscoresscreen = Assets.startscreen
        Assets.gameoverscreen = g.newPixmap("gameover.png", Graphics.PixmapFormat.RGB565)

        // Menus
        Assets.mainmenu = g.newPixmap("mainmenu.png", Graphics.PixmapFormat.RGB565)
        Assets.pausemenu = g.newPixmap("pausemenu.png", Graphics.PixmapFormat.RGB565)
        Assets.readymenu = g.newPixmap("ready.png", Graphics.PixmapFormat.ARGB4444)

        Assets.redblock = g.newPixmap("redblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.greenblock = g.newPixmap("greenblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.blueblock = g.newPixmap("blueblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.cyanblock = g.newPixmap("cyanblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.yellowblock = g.newPixmap("yellowblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.magentablock = g.newPixmap("magentablock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.orangeblock = g.newPixmap("orangeblock.png", Graphics.PixmapFormat.ARGB4444)

        Assets.smallredblock = g.newPixmap("smallredblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.smallgreenblock = g.newPixmap("smallgreenblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.smallblueblock = g.newPixmap("smallblueblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.smallcyanblock = g.newPixmap("smallcyanblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.smallyellowblock = g.newPixmap("smallyellowblock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.smallmagentablock = g.newPixmap("smallmagentablock.png", Graphics.PixmapFormat.ARGB4444)
        Assets.smallorangeblock = g.newPixmap("smallorangeblock.png", Graphics.PixmapFormat.ARGB4444)

        Assets.buttons = g.newPixmap("buttons.png", Graphics.PixmapFormat.RGB565)
        Assets.numbers = g.newPixmap("numbers.png", Graphics.PixmapFormat.ARGB4444)

        // Audio effects
        Assets.click = Gdx.audio!!.newSound("click.ogg")
        Assets.bitten = Gdx.audio!!.newSound("bitten.ogg")

        // Music
        Assets.music = Gdx.audio!!.newMusic("Korobeiniki.ogg")

        Settings.load(Gdx.fileIO!!)
        Gdx.game!!.setScreen(StartScreen())
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
}
