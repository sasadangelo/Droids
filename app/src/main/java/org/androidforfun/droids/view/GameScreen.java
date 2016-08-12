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

    @Override
    public void update(float deltaTime) {
        Log.i(LOG_TAG, "update -- begin");
        Input input = Gdx.input;

        List<TouchEvent> touchEvents = input.getTouchEvents();
        input.getKeyEvents();

        states.get(DroidsWorld.getInstance().getState()).update(touchEvents, deltaTime);
    }

    @Override
    public void draw(float deltaTime) {
        Log.i(LOG_TAG, "draw -- begin");
        states.get(DroidsWorld.getInstance().getState()).draw();
    }

    public void drawText(Graphics g, String line, int x, int y) {
        Log.i(LOG_TAG, "drawText -- begin");
        int len = line.length();
        for (int i = 0; i < len; i++) {
            char character = line.charAt(i);

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

            g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
            x += srcWidth;
        }
    }

    @Override
    public void pause() {
        Log.i(LOG_TAG, "pause -- begin");

        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Running)
            DroidsWorld.getInstance().setState(DroidsWorld.GameState.Paused);

        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.GameOver) {
            Settings.addScore(DroidsWorld.getInstance().getScore());
            Settings.save(Gdx.fileIO);
        }
    }

    abstract class GameState {
        abstract void update(List<TouchEvent> touchEvents, float deltaTime);
        abstract void draw();
    }

    class GameRunning extends GameState {
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

        void draw() {
            Log.i(LOG_TAG, "GameRunning.draw -- begin");
            Graphics g = Gdx.graphics;

            g.drawPixmap(Assets.gamescreen, gameScreenBounds.getX(), gameScreenBounds.getY());
            renderer.draw();
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

            TextStyle style = new TextStyle();
            style.setColor(0xffffffff);
            style.setTextSize(10);
            style.setAlign(TextStyle.Align.CENTER);
            g.drawText("" + DroidsWorld.getInstance().getLevel(), 30 + leftRegion.getX(), 165 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getGoal(), 30 + leftRegion.getX(), 265 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getScore(), 30 + rightRegion.getX(), 265 + rightRegion.getY(), style);
        }
    }

    class GamePaused extends GameState {
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GamePaused.update -- begin");
            Game game = Gdx.game;

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
            if(Settings.soundEnabled)
                if (Assets.music.isPlaying())
                    Assets.music.pause();
        }

        void draw() {
            Log.i(LOG_TAG, "GamePaused.draw -- begin");
            Graphics g = Gdx.graphics;

            g.drawPixmap(Assets.gamescreen, gameScreenBounds.getX(), gameScreenBounds.getY());
            g.drawPixmap(Assets.pausemenu, pauseMenuBounds.getX(), pauseMenuBounds.getY());
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

            TextStyle style = new TextStyle();
            style.setColor(0xffffffff);
            style.setTextSize(10);
            style.setAlign(TextStyle.Align.CENTER);
            g.drawText("" + DroidsWorld.getInstance().getLevel(), 30 + leftRegion.getX(), 165 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getGoal(), 30 + leftRegion.getX(), 265 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getScore(), 30 + rightRegion.getX(), 265 + rightRegion.getY(), style);
        }
    }

    class GameReady extends GameState {
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GameReady.update -- begin");
            if(touchEvents.size() > 0)
                DroidsWorld.getInstance().setState(DroidsWorld.GameState.Running);
        }

        void draw() {
            Log.i(LOG_TAG, "GameReady.draw -- begin");
            Graphics g = Gdx.graphics;

            g.drawPixmap(Assets.gamescreen, gameScreenBounds.getX(), gameScreenBounds.getY());
            g.drawPixmap(Assets.readymenu, readyMenuBounds.getX(), readyMenuBounds.getY());
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

    class GameOver extends GameState {
        void update(List<TouchEvent> touchEvents, float deltaTime) {
            Log.i(LOG_TAG, "GameOver.update -- begin");
            Game game = Gdx.game;

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
            if(Settings.soundEnabled)
                if (Assets.music.isPlaying())
                    Assets.music.stop();
        }

        void draw() {
            Log.i(LOG_TAG, "GameOver.draw -- begin");
            Graphics g = Gdx.graphics;

            g.drawPixmap(Assets.gameoverscreen, gameoverScreenBounds.getX(), gameoverScreenBounds.getY());
            g.drawPixmap(Assets.buttons, xButtonBounds.getX(), xButtonBounds.getY(), 0, 100,
                    xButtonBounds.getWidth()+1, xButtonBounds.getHeight()+1); // down button
            drawText(g, "" + DroidsWorld.getInstance().getScore(), 180, 280);
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

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