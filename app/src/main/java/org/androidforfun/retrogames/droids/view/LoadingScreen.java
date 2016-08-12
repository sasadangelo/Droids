package org.androidforfun.retrogames.droids.view;

import android.util.Log;

import org.androidforfun.framework.Game;
import org.androidforfun.framework.Gdx;
import org.androidforfun.framework.Graphics;
import org.androidforfun.framework.Graphics.PixmapFormat;
import org.androidforfun.framework.Screen;
import org.androidforfun.retrogames.droids.model.Settings;

public class LoadingScreen implements Screen {
    private static final String LOG_TAG = "Droids.LoadingScreen";

    @Override
    public void update(float deltaTime) {
        Log.i(LOG_TAG, "update -- begin");
        Game game = Gdx.game;
        Graphics g = Gdx.graphics;

        Assets.logo = g.newPixmap("logo.png", PixmapFormat.RGB565);

        Assets.startscreen = g.newPixmap("startscreen.png", PixmapFormat.RGB565);
        Assets.gamescreen = g.newPixmap("gamescreen.png", PixmapFormat.RGB565);
        Assets.highscoresscreen = Assets.startscreen;
        Assets.gameoverscreen = g.newPixmap("gameover.png", PixmapFormat.RGB565);

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

        Assets.click = game.getAudio().newSound("click.ogg");
        Assets.bitten = game.getAudio().newSound("bitten.ogg");
        Assets.music = game.getAudio().newMusic("Korobeiniki.ogg");

        Settings.load(Gdx.fileIO);
        game.setScreen(new StartScreen());
    }

    @Override
    public void draw(float deltaTime) {
        Log.i(LOG_TAG, "draw -- begin");
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}