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

import org.androidforfun.framework.Rectangle;
import org.androidforfun.droids.model.Settings;
import org.androidforfun.framework.Game;
import org.androidforfun.framework.Gdx;
import org.androidforfun.framework.Graphics;
import org.androidforfun.framework.Input;
import org.androidforfun.framework.Screen;

import java.util.List;

/*
 * This class represents the start screen. It contains the logo and the main menu with three
 * options:
 *     Play
 *     Highscores
 *     Quit
 *
 *  It also has a button to activate/deactivate sound.
 *
 * @author Salvatore D'Angelo
 */
public class StartScreen implements Screen {
    private Rectangle backgroundBounds;
    private Rectangle logoBounds;
    private Rectangle soundButtonBounds;
    private Rectangle mainMenuBounds;
    private Rectangle playMenuBounds;
    private Rectangle highscoresMenuBounds;
    private Rectangle quitMenuBounds;

    public StartScreen() {
        backgroundBounds=new Rectangle(0, 0, 320, 480);
        logoBounds=new Rectangle(32, 20, 256, 160);
        soundButtonBounds=new Rectangle(32, 370, 50, 50);
        mainMenuBounds=new Rectangle(84, 220, 153, 124);
        playMenuBounds=new Rectangle(64, 220, 192, 42);
        highscoresMenuBounds=new Rectangle(64, 220 + 42, 192, 42);
        quitMenuBounds=new Rectangle(64, 220 + 84, 192, 42);
    }

    /*
     * Check the user input and if one the the folloing things could occurs:
     *     - Play the game
     *     - See Highscores
     *     - Quit game
     *     - Activate/deactivate sound
     */
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = Gdx.input.getTouchEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_UP) {
                // activate/deactivate sound
                if(soundButtonBounds.contains(event.x, event.y)) {
                    Settings.soundEnabled = !Settings.soundEnabled;
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                }
                // play the game
                if(playMenuBounds.contains(event.x, event.y)) {
                    Gdx.game.setScreen(new GameScreen());
                    if (Settings.soundEnabled)
                        Assets.click.play(1);
                    return;
                }
                // see highscores.
                if(highscoresMenuBounds.contains(event.x, event.y)) {
                    Gdx.game.setScreen(new HighscoreScreen());
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                    return;
                }
                // quit the game.
                if(quitMenuBounds.contains(event.x, event.y)) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                    System.exit(1);
                    return;
                }
            }
        }
    }

    /*
     * Draw the start screen.
     */
    public void draw(float deltaTime) {
        Graphics g = Gdx.graphics;

        // draw the background
        g.drawPixmap(Assets.startscreen, backgroundBounds.getX(), backgroundBounds.getY());
        // draw the logo
        g.drawPixmap(Assets.logo, logoBounds.getX(), logoBounds.getY());
        // draw the main menu
        g.drawPixmap(Assets.mainmenu, mainMenuBounds.getX(), mainMenuBounds.getY());
        // draw the sound button depending on sound status.
        if(Settings.soundEnabled)
            g.drawPixmap(Assets.buttons, soundButtonBounds.getX(), soundButtonBounds.getY(), 0, 0,
                    soundButtonBounds.getWidth()+1, soundButtonBounds.getHeight()+1);
        else
            g.drawPixmap(Assets.buttons, soundButtonBounds.getX(), soundButtonBounds.getY(), 50, 0,
                    soundButtonBounds.getWidth()+1, soundButtonBounds.getHeight()+1);
    }

    /*
     * The screen is paused.
     */
    public void pause() {
        Settings.save(Gdx.fileIO);
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
