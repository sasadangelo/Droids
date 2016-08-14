/*
 *  Copyright (C) 2016 Salvatore D'Angelo
 *  This file is part of Droids project.
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

import org.androidforfun.droids.model.DroidsWorld;
import org.androidforfun.droids.model.Settings;
import org.androidforfun.framework.Game;
import org.androidforfun.framework.Gdx;
import org.androidforfun.framework.Graphics;
import org.androidforfun.framework.Input;
import org.androidforfun.framework.Input.TouchEvent;
import org.androidforfun.framework.Rectangle;
import org.androidforfun.framework.Screen;
import org.androidforfun.framework.TextStyle;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/*
 * GameScreen
 *
 * This class represents the game screen. The processing and rendering depends on the game state managed
 * by State pattern. The update and draw method are delegated to:
 *    GamePause.update, GamePause.draw
 *    GameReady.update, GameReady.draw
 *    GameRunning.update, GameRunning.draw
 *    GameOver.update, GameOver.draw
 *
 * depending on the status of the game.
 *
 * @author Salvatore D'Angelo
 */
public class GameScreen implements Screen {
    private static final String LOG_TAG = "Droids.GameScreen";

    private Map<DroidsWorld.GameState, GameState> states = new EnumMap<>(DroidsWorld.GameState.class);
    private Rectangle leftRegion;
    private Rectangle rightRegion;
    private Rectangle workingRegion;
    private Rectangle commandRegion;

    private Rectangle gameoverScreenBounds;
    private Rectangle gameScreenBounds;
    private Rectangle pauseButtonBounds;
    private Rectangle leftButtonBounds;
    private Rectangle rightButtonBounds;
    private Rectangle rotateButtonBounds;
    private Rectangle downButtonBounds;
    private Rectangle xButtonBounds;
    private Rectangle pauseMenuBounds;
    private Rectangle resumeMenuBounds;
    private Rectangle readyMenuBounds;
    private Rectangle homeMenuBounds;

    private DroidsWorldRenderer renderer;

    public GameScreen() {
        Log.i(LOG_TAG, "constructor -- begin");

        states.put(DroidsWorld.GameState.Paused, new GamePaused());
        states.put(DroidsWorld.GameState.Ready, new GameReady());
        states.put(DroidsWorld.GameState.Running, new GameRunning());
        states.put(DroidsWorld.GameState.GameOver, new GameOver());

        leftRegion = new Rectangle(0, 0, 60, 400);
        workingRegion = new Rectangle(60, 20, 200, 400);
        rightRegion = new Rectangle(260, 0, 60, 400);
        commandRegion = new Rectangle(0, 400, 320, 80);

        gameoverScreenBounds=new Rectangle(0, 0, 320, 480);
        gameScreenBounds=new Rectangle(0, 0, 320, 480);
        pauseButtonBounds=new Rectangle(5, 20, 50, 50);
        leftButtonBounds=new Rectangle(30, 425, 50, 50);
        rightButtonBounds=new Rectangle(240, 425, 50, 50);
        rotateButtonBounds=new Rectangle(100, 425, 50, 50);
        downButtonBounds=new Rectangle(170, 425, 50, 50);
        pauseMenuBounds=new Rectangle(100, 100, 105, 68);
        resumeMenuBounds=new Rectangle(80, 100, 160, 48);
        readyMenuBounds=new Rectangle(65, 100, 188, 70);
        homeMenuBounds=new Rectangle(80, 148, 160, 48);
        xButtonBounds=new Rectangle(128, 200, 50, 50);

        renderer = new DroidsWorldRenderer();
    }

    /*
     * The update method is delegated to:
     *    GamePause.update
     *    GameReady.update
     *    GameRunning.update
     *    GameOver.update
     *
     * depending on the status of the game.
     */
    public void update(float deltaTime) {
        Log.i(LOG_TAG, "update -- begin");
        Input input = Gdx.input;

        List<TouchEvent> touchEvents = input.getTouchEvents();
        input.getKeyEvents();

        states.get(DroidsWorld.getInstance().getState()).update(touchEvents, deltaTime);
    }

    /*
     * The draw method is delegated to:
     *    GamePause.draw
     *    GameReady.draw
     *    GameRunning.draw
     *    GameOver.draw
     *
     * depending on the status of the game.
     */
    public void draw(float deltaTime) {
        Log.i(LOG_TAG, "draw -- begin");
        states.get(DroidsWorld.getInstance().getState()).draw();
    }

    /*
     * Draw text on the scree in the (x, y) position.
     */
    public void drawText(String text, int x, int y) {
        Log.i(LOG_TAG, "drawText -- begin");
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char character = text.charAt(i);

            if (character == ' ') {
                x += 20;
                continue;
            }

            int srcX;
            int srcWidth;
            if (character == '.') {
                srcX = 200;
                srcWidth = 10;
            } else {
                srcX = (character - '0') * 20;
                srcWidth = 20;
            }

            Gdx.graphics.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
            x += srcWidth;
        }
    }

    /*
     * The screen is paused.
     */
    public void pause() {
        Log.i(LOG_TAG, "pause -- begin");

        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Running)
            DroidsWorld.getInstance().setState(DroidsWorld.GameState.Paused);

        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.GameOver) {
            Settings.addScore(DroidsWorld.getInstance().getScore());
            Settings.save(Gdx.fileIO);
        }
    }

    /*
     * The abstract class representing a generic State. Used to implement the State pattern.
     */
    abstract class GameState {
        abstract void update(List<TouchEvent> touchEvents, float deltaTime);
        abstract void draw();
    }

    /*
     * GameRunning
     *
     * This class represents the game screen in running state. It will be responsible to update and
     * draw when the game is running.
     *
     * @author Salvatore D'Angelo
     */
    class GameRunning extends GameState {
        /*
         * Update the game when it is in running state. The method catch the user input and
         * depending on it will move, rotate or accelerate the falling shape. It can also pause the
         * game and check for game over.
         */
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GameRunning.update -- begin");
            int len = touchEvents.size();
            for(int i = 0; i < len; i++) {
                TouchEvent event = touchEvents.get(i);
                if(event.type == TouchEvent.TOUCH_UP) {
                    if(pauseButtonBounds.contains(event.x, event.y)) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        DroidsWorld.getInstance().setState(DroidsWorld.GameState.Paused);
                        return;
                    }
                }
                if(event.type == TouchEvent.TOUCH_DOWN) {
                    // Move falling shape on the left, if possible
                    if(leftButtonBounds.contains(event.x, event.y)) {
                        DroidsWorld.getInstance().getFallingShape().moveLeft();
                        if (DroidsWorld.getInstance().getFallingShape().collide())
                            DroidsWorld.getInstance().getFallingShape().moveRight();
                    }
                    // Move falling shape on the right, if possible
                    if(rightButtonBounds.contains(event.x, event.y)) {
                        DroidsWorld.getInstance().getFallingShape().moveRight();
                        if (DroidsWorld.getInstance().getFallingShape().collide())
                            DroidsWorld.getInstance().getFallingShape().moveLeft();
                    }
                    // Rotate falling shape, if possible
                    if(rotateButtonBounds.contains(event.x, event.y)) {
                        DroidsWorld.getInstance().getFallingShape().rotate();
                        if (DroidsWorld.getInstance().getFallingShape().collide())
                            DroidsWorld.getInstance().getFallingShape().undoRotate();
                    }
                    // Accelerate falling of the falling shape
                    if(downButtonBounds.contains(event.x, event.y)) {
                        DroidsWorld.getInstance().getFallingShape().accelerateFalling();
                    }
                }
            }

            DroidsWorld.getInstance().update(deltaTime);
            if (DroidsWorld.getInstance().getState() == DroidsWorld.GameState.GameOver) {
                if(Settings.soundEnabled)
                    Assets.bitten.play(1);
            }
            if(Settings.soundEnabled)
                if (!Assets.music.isPlaying()) {
                    Assets.music.setLooping(true);
                    Assets.music.play();
            }
        }

        /*
         * Draw the game in running state.
         */
        void draw() {
            Log.i(LOG_TAG, "GameRunning.draw -- begin");
            Graphics g = Gdx.graphics;

            // draw the background
            g.drawPixmap(Assets.gamescreen, gameScreenBounds.getX(), gameScreenBounds.getY());
            // render the game world.
            renderer.draw();
            // draw the buttons.
            g.drawPixmap(Assets.buttons, pauseButtonBounds.getX(), pauseButtonBounds.getY(), 50, 100,
                    pauseButtonBounds.getWidth()+1, pauseButtonBounds.getHeight()+1); // pause button
            g.drawPixmap(Assets.buttons, leftButtonBounds.getX(), leftButtonBounds.getY(), 50, 50,
                    leftButtonBounds.getWidth()+1, leftButtonBounds.getHeight()+1);  // left button
            g.drawPixmap(Assets.buttons, rightButtonBounds.getX(), rightButtonBounds.getY(), 0, 50,
                    rightButtonBounds.getWidth()+1, rightButtonBounds.getHeight()+1); // right button
            g.drawPixmap(Assets.buttons, rotateButtonBounds.getX(), rotateButtonBounds.getY(), 50, 150,
                    rotateButtonBounds.getWidth()+1, rotateButtonBounds.getHeight()+1); // rotate button
            g.drawPixmap(Assets.buttons, downButtonBounds.getX(), downButtonBounds.getY(), 0, 150,
                    downButtonBounds.getWidth()+1, downButtonBounds.getHeight()+1); // down button

            // draw the goal, score and level.
            TextStyle style = new TextStyle();
            style.setColor(0xffffffff);
            style.setTextSize(10);
            style.setAlign(TextStyle.Align.CENTER);
            g.drawText("" + DroidsWorld.getInstance().getLevel(), 30 + leftRegion.getX(), 165 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getGoal(), 30 + leftRegion.getX(), 265 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getScore(), 30 + rightRegion.getX(), 265 + rightRegion.getY(), style);
        }
    }

    /*
     * GamePaused
     *
     * This class represents the game screen in pause state. It will be responsible to update and
     * draw when the game is paused.
     *
     * @author Salvatore D'Angelo
     */
    class GamePaused extends GameState {
        /*
         * Update the game when it is in paused state. The method catch the user input and
         * depending on it will resume the game or return to the start screen.
         */
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GamePaused.update -- begin");
            Game game = Gdx.game;

            // Check if user asked to resume the game or come back to the start screen.
            int len = touchEvents.size();
            for(int i = 0; i < len; i++) {
                TouchEvent event = touchEvents.get(i);
                if(event.type == TouchEvent.TOUCH_UP) {
                    if(resumeMenuBounds.contains(event.x, event.y)) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        DroidsWorld.getInstance().setState(DroidsWorld.GameState.Running);
                        return;
                    }
                    if(homeMenuBounds.contains(event.x, event.y)) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        game.setScreen(new StartScreen());
                        return;
                    }
                }
            }
            // pause the music if it is playing.
            if(Settings.soundEnabled)
                if (Assets.music.isPlaying())
                    Assets.music.pause();
        }

        /*
         * Draw the game in paused state.
         */
        void draw() {
            Log.i(LOG_TAG, "GamePaused.draw -- begin");
            Graphics g = Gdx.graphics;

            // draw the background
            g.drawPixmap(Assets.gamescreen, gameScreenBounds.getX(), gameScreenBounds.getY());
            // draw the pause menu
            g.drawPixmap(Assets.pausemenu, pauseMenuBounds.getX(), pauseMenuBounds.getY());
            // draw buttons
            g.drawPixmap(Assets.buttons, pauseButtonBounds.getX(), pauseButtonBounds.getY(), 50, 100,
                    pauseButtonBounds.getWidth()+1, pauseButtonBounds.getHeight()+1); // pause button
            g.drawPixmap(Assets.buttons, leftButtonBounds.getX(), leftButtonBounds.getY(), 50, 50,
                    leftButtonBounds.getWidth()+1, leftButtonBounds.getHeight()+1);  // left button
            g.drawPixmap(Assets.buttons, rightButtonBounds.getX(), rightButtonBounds.getY(), 0, 50,
                    rightButtonBounds.getWidth()+1, rightButtonBounds.getHeight()+1); // right button
            g.drawPixmap(Assets.buttons, rotateButtonBounds.getX(), rotateButtonBounds.getY(), 50, 150,
                    rotateButtonBounds.getWidth()+1, rotateButtonBounds.getHeight()+1); // rotate button
            g.drawPixmap(Assets.buttons, downButtonBounds.getX(), downButtonBounds.getY(), 0, 150,
                    downButtonBounds.getWidth()+1, downButtonBounds.getHeight()+1); // down button

            // draw the goal, score and level.
            TextStyle style = new TextStyle();
            style.setColor(0xffffffff);
            style.setTextSize(10);
            style.setAlign(TextStyle.Align.CENTER);
            g.drawText("" + DroidsWorld.getInstance().getLevel(), 30 + leftRegion.getX(), 165 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getGoal(), 30 + leftRegion.getX(), 265 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getScore(), 30 + rightRegion.getX(), 265 + rightRegion.getY(), style);
        }
    }

    /*
     * GameReady
     *
     * This class represents the game screen in ready state. It will be responsible to update and
     * draw when the game is ready.
     *
     * @author Salvatore D'Angelo
     */
    class GameReady extends GameState {
        /*
         * Update the game when it is in ready state. The method catch the user input and
         * resume the game.
         */
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GameReady.update -- begin");
            if(touchEvents.size() > 0)
                DroidsWorld.getInstance().setState(DroidsWorld.GameState.Running);
        }

        /*
         * Draw the game in ready state.
         */
        void draw() {
            Log.i(LOG_TAG, "GameReady.draw -- begin");
            Graphics g = Gdx.graphics;

            // draw the background
            g.drawPixmap(Assets.gamescreen, gameScreenBounds.getX(), gameScreenBounds.getY());
            // draw the ready menu
            g.drawPixmap(Assets.readymenu, readyMenuBounds.getX(), readyMenuBounds.getY());
            // draw buttons
            g.drawPixmap(Assets.buttons, pauseButtonBounds.getX(), pauseButtonBounds.getY(), 50, 100,
                    pauseButtonBounds.getWidth()+1, pauseButtonBounds.getHeight()+1); // pause button
            g.drawPixmap(Assets.buttons, leftButtonBounds.getX(), leftButtonBounds.getY(), 50, 50,
                    leftButtonBounds.getWidth()+1, leftButtonBounds.getHeight()+1);  // left button
            g.drawPixmap(Assets.buttons, rightButtonBounds.getX(), rightButtonBounds.getY(), 0, 50,
                    rightButtonBounds.getWidth()+1, rightButtonBounds.getHeight()+1); // right button
            g.drawPixmap(Assets.buttons, rotateButtonBounds.getX(), rotateButtonBounds.getY(), 50, 150,
                    rotateButtonBounds.getWidth()+1, rotateButtonBounds.getHeight()+1); // rotate button
            g.drawPixmap(Assets.buttons, downButtonBounds.getX(), downButtonBounds.getY(), 0, 150,
                    downButtonBounds.getWidth()+1, downButtonBounds.getHeight()+1); // down button
        }
    }

    /*
     * GameOver
     *
     * This class represents the game screen when it is over. It will be responsible to update and
     * draw when the game is over.
     *
     * @author Salvatore D'Angelo
     */
    class GameOver extends GameState {
        /*
         * Update the game when it is over. The method catch the user input and return to the
         * start screen.
         */
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GameOver.update -- begin");
            Game game = Gdx.game;

            // check if the x button is pressed.
            int len = touchEvents.size();
            for(int i = 0; i < len; i++) {
                TouchEvent event = touchEvents.get(i);
                if(event.type == TouchEvent.TOUCH_UP) {
                    if(xButtonBounds.contains(event.x, event.y)) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        game.setScreen(new StartScreen());
                        DroidsWorld.getInstance().clear();
                        return;
                    }
                }
            }
            // pause the music if it is playing.
            if(Settings.soundEnabled)
                if (Assets.music.isPlaying())
                    Assets.music.stop();
        }

        /*
         * Draw the game when it si over.
         */
        void draw() {
            Log.i(LOG_TAG, "GameOver.draw -- begin");
            Graphics g = Gdx.graphics;

            g.drawPixmap(Assets.gameoverscreen, gameoverScreenBounds.getX(), gameoverScreenBounds.getY());
            g.drawPixmap(Assets.buttons, xButtonBounds.getX(), xButtonBounds.getY(), 0, 100,
                    xButtonBounds.getWidth()+1, xButtonBounds.getHeight()+1); // down button
            drawText("" + DroidsWorld.getInstance().getScore(), 180, 280);
        }
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

    /*
     * The screen is disposed.
     */
    public Rectangle getLeftRegion() {
        return leftRegion;
    }
    public Rectangle getRightRegion() {
        return rightRegion;
    }
    public Rectangle getWorkingRegion() {
        return workingRegion;
    }
    public Rectangle getCommandRegion() {
        return commandRegion;
    }
}