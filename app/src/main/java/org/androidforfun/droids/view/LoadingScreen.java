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

import android.util.Log;

import org.androidforfun.framework.Game;
import org.androidforfun.framework.Gdx;
import org.androidforfun.framework.Graphics;
import org.androidforfun.framework.Graphics.PixmapFormat;
import org.androidforfun.framework.Screen;
import org.androidforfun.droids.model.Settings;

/*
 * This class represents the loading screen. It load in memory all the assets used by the game.
 * Usually games show a progress bar in this screen. To simplify the code and since the assets are
 * loaded very quickly I avoided this complication.
 *
 * @author Salvatore D'Angelo
 */
public class LoadingScreen implements Screen {
    private static final String LOG_TAG = "Droids.LoadingScreen";

    @Override
    public void update(float deltaTime) {
        Log.i(LOG_TAG, "update -- begin");
        Graphics g = Gdx.graphics;

        Assets.gamescreen = g.newPixmap("gamescreen.png", PixmapFormat.RGB565);
        Assets.logo = g.newPixmap("logo.png", PixmapFormat.RGB565);

        // Screens
        Assets.startscreen = g.newPixmap("startscreen.png", PixmapFormat.RGB565);
        Assets.highscoresscreen = Assets.startscreen;
        Assets.gameoverscreen = g.newPixmap("gameover.png", PixmapFormat.RGB565);

        // Menus
        Assets.mainmenu = g.newPixmap("mainmenu.png", PixmapFormat.RGB565);
        Assets.pausemenu = g.newPixmap("pausemenu.png", PixmapFormat.RGB565);
        Assets.readymenu = g.newPixmap("ready.png", PixmapFormat.ARGB4444);

        Assets.redblock = g.newPixmap("redblock.png", PixmapFormat.ARGB4444);
        Assets.greenblock = g.newPixmap("greenblock.png", PixmapFormat.ARGB4444);
        Assets.blueblock = g.newPixmap("blueblock.png", PixmapFormat.ARGB4444);
        Assets.cyanblock = g.newPixmap("cyanblock.png", PixmapFormat.ARGB4444);
        Assets.yellowblock = g.newPixmap("yellowblock.png", PixmapFormat.ARGB4444);
        Assets.magentablock = g.newPixmap("magentablock.png", PixmapFormat.ARGB4444);
        Assets.orangeblock = g.newPixmap("orangeblock.png", PixmapFormat.ARGB4444);

        Assets.smallredblock = g.newPixmap("smallredblock.png", PixmapFormat.ARGB4444);
        Assets.smallgreenblock = g.newPixmap("smallgreenblock.png", PixmapFormat.ARGB4444);
        Assets.smallblueblock = g.newPixmap("smallblueblock.png", PixmapFormat.ARGB4444);
        Assets.smallcyanblock = g.newPixmap("smallcyanblock.png", PixmapFormat.ARGB4444);
        Assets.smallyellowblock = g.newPixmap("smallyellowblock.png", PixmapFormat.ARGB4444);
        Assets.smallmagentablock = g.newPixmap("smallmagentablock.png", PixmapFormat.ARGB4444);
        Assets.smallorangeblock = g.newPixmap("smallorangeblock.png", PixmapFormat.ARGB4444);

        Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.RGB565);
        Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);

        // Audio effects
        Assets.click = Gdx.audio.newSound("click.ogg");
        Assets.bitten = Gdx.audio.newSound("bitten.ogg");

        // Music
        Assets.music = Gdx.audio.newMusic("Korobeiniki.ogg");

        Settings.load(Gdx.fileIO);
        Gdx.game.setScreen(new StartScreen());
    }

    /*
     * Draw nothing.
     */
    public void draw(float deltaTime) {
        Log.i(LOG_TAG, "draw -- begin");
    }

    /*
     * The screen is paused.
     */
    public void pause() {
    }

    /*
     * The screen is resumed.
     */
    public void resume() {
    }

    /*
     * The screen is disposed.
     */
    public void dispose() {
    }
}