package org.androidforfun.retrogames.droids.view;

import android.util.Log;

import org.androidforfun.retrogames.droids.model.Block;
import org.androidforfun.retrogames.droids.model.DroidsWorld;
import org.androidforfun.retrogames.droids.model.Settings;
import org.androidforfun.retrogames.droids.model.ShapeCube;
import org.androidforfun.retrogames.droids.model.ShapeI;
import org.androidforfun.retrogames.droids.model.ShapeJ;
import org.androidforfun.retrogames.droids.model.ShapeL;
import org.androidforfun.retrogames.droids.model.ShapeS;
import org.androidforfun.retrogames.droids.model.ShapeT;
import org.androidforfun.retrogames.droids.model.ShapeZ;
import org.androidforfun.retrogames.framework.Game;
import org.androidforfun.retrogames.framework.Graphics;
import org.androidforfun.retrogames.framework.Input.TouchEvent;
import org.androidforfun.retrogames.framework.Screen;
import org.androidforfun.retrogames.framework.TextStyle;

import java.util.List;

public class GameScreen extends Screen {
    public static int BLOCK_WIDTH=20;
    public static int BLOCK_HEIGHT=20;

    private static final String LOG_TAG = "Droids.GameScreen";
    private Region leftRegion;
    private Region rightRegion;
    private Region workingRegion;
    private Region commandRegion;

    public GameScreen(Game game) {
        super(game);
        Log.i(LOG_TAG, "constructor -- begin");
        leftRegion = new Region(0, 0, 60, 400);
        workingRegion = new Region(60, 20, 200, 400);
        rightRegion = new Region(260, 0, 60, 400);
        commandRegion = new Region(0, 400, 320, 80);
    }

    @Override
    public void update(float deltaTime) {
        Log.i(LOG_TAG, "update -- begin");
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Ready)
            updateReady(touchEvents);
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Paused)
            updatePaused(touchEvents);
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.GameOver)
            updateGameOver(touchEvents);        
    }
    
    private void updateReady(List<TouchEvent> touchEvents) {
        Log.i(LOG_TAG, "updateReady -- begin");
        if(touchEvents.size() > 0)
            DroidsWorld.getInstance().setState(DroidsWorld.GameState.Running);
    }
    
    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        Log.i(LOG_TAG, "updateRunning -- begin");
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x >= 5 && event.x < 55 &&
                        event.y >= 20 && event.y < 70) {
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                    DroidsWorld.getInstance().setState(DroidsWorld.GameState.Paused);
                    return;
                }
            }
            if(event.type == TouchEvent.TOUCH_DOWN) {
                // Move falling shape on the left, if possible
                if(event.x >= 20 && event.x < 70 &&
                        event.y >= 425 && event.y < 475) {
                    DroidsWorld.getInstance().getFallingShape().moveLeft();
                    if (DroidsWorld.getInstance().getFallingShape().collide())
                        DroidsWorld.getInstance().getFallingShape().moveRight();
                }
                // Move falling shape on the right, if possible
                if(event.x >= 230 && event.x < 280 &&
                        event.y >= 425 && event.y < 475) {
                    DroidsWorld.getInstance().getFallingShape().moveRight();
                    if (DroidsWorld.getInstance().getFallingShape().collide())
                        DroidsWorld.getInstance().getFallingShape().moveLeft();
                }
                // Rotate falling shape, if possible
                if(event.x >= 90 && event.x < 140 &&
                        event.y >= 425 && event.y < 475) {
                    DroidsWorld.getInstance().getFallingShape().rotateRight();
                    if (DroidsWorld.getInstance().getFallingShape().collide())
                        DroidsWorld.getInstance().getFallingShape().rotateLeft();
                }
                // Accelerate falling of the falling shape
                if(event.x >= 160 && event.x < 210 &&
                        event.y >= 425 && event.y < 475) {
                    DroidsWorld.getInstance().getFallingShape().accelerateFalling();
                }
            }
        }

        DroidsWorld.getInstance().update(deltaTime);
        if (DroidsWorld.getInstance().getState() == DroidsWorld.GameState.GameOver) {
            if(Settings.soundEnabled)
                Assets.bitten.play(1);
            DroidsWorld.getInstance().setState(DroidsWorld.GameState.GameOver);
        }
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        Log.i(LOG_TAG, "updatePaused -- begin");
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x > 80 && event.x <= 240) {
                    if(event.y > 100 && event.y <= 148) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        DroidsWorld.getInstance().setState(DroidsWorld.GameState.Running);
                        return;
                    }
                    if(event.y > 148 && event.y < 196) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        game.setScreen(new StartScreen(game));
                        return;
                    }
                }
            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        Log.i(LOG_TAG, "updateGameOver -- begin");
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x >= 128 && event.x <= 192 && event.y >= 200 && event.y <= 264) {
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                    game.setScreen(new StartScreen(game));
                    DroidsWorld.getInstance().clear();
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Log.i(LOG_TAG, "present -- begin");
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.gamescreen, 0, 0);
        drawTetris();
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Ready)
            drawReadyUI();
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Running)
            drawRunningUI();
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.Paused)
            drawPausedUI();
        if(DroidsWorld.getInstance().getState() == DroidsWorld.GameState.GameOver)
            drawGameOverUI();

        if (DroidsWorld.getInstance().getState() != DroidsWorld.GameState.GameOver) {
            TextStyle style = new TextStyle();
            style.setColor(0xffffffff);
            style.setTextSize(10);
            style.setAlign(TextStyle.Align.CENTER);
            g.drawText("" + DroidsWorld.getInstance().getLevel(), 30 + leftRegion.getX(), 165 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getGoal(), 30 + leftRegion.getX(), 265 + leftRegion.getY(), style);
            g.drawText("" + DroidsWorld.getInstance().getScore(), 30 + rightRegion.getX(), 265 + rightRegion.getY(), style);
        }
    }

    private void drawTetris() {
        Graphics g = game.getGraphics();

        for (Block block : DroidsWorld.getInstance().getBlocks()) {
            int x = workingRegion.getX() + block.getX()*BLOCK_WIDTH;
            int y = workingRegion.getY() + block.getY()*BLOCK_HEIGHT;
            g.drawPixmap(Assets.getBlockByColor(block.getColor()), x, y);
        }

        for (Block block : DroidsWorld.getInstance().getFallingShape().getBlocks()) {
            int x = workingRegion.getX() + block.getX()*BLOCK_WIDTH;
            int y = workingRegion.getY() + block.getY()*BLOCK_HEIGHT;
            g.drawPixmap(Assets.getBlockByColor(block.getColor()), x, y);
        }

        // This for loop draw the Next Shape in the Game Screen on the top right side
        for (Block block : DroidsWorld.getInstance().getNextShape().getBlocks()) {
            int x = block.getX()*16;
            int y = block.getY()*16;

            if (DroidsWorld.getInstance().getNextShape() instanceof ShapeCube ||
                    DroidsWorld.getInstance().getNextShape() instanceof ShapeJ) {
                x+=15;
            } else if (DroidsWorld.getInstance().getNextShape() instanceof ShapeI) {
                x+=25;
            } else if (DroidsWorld.getInstance().getNextShape() instanceof ShapeL) {
                x+=20;
            } else if (DroidsWorld.getInstance().getNextShape() instanceof ShapeS ||
                    DroidsWorld.getInstance().getNextShape() instanceof ShapeT ||
                    DroidsWorld.getInstance().getNextShape() instanceof ShapeZ) {
                x+=5;
            }

            g.drawPixmap(Assets.getSmallBlockByColor(block.getColor()), rightRegion.getX() + x, rightRegion.getY() + 65 + y);
        }
    }

    private void drawReadyUI() {
        Log.i(LOG_TAG, "drawReadyUI -- begin");
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.readymenu, 65, 100);
        g.drawPixmap(Assets.buttons, 20, 425, 50, 50, 51, 51); // left button
        g.drawPixmap(Assets.buttons, 230, 425, 0, 50, 51, 51); // right button
        g.drawPixmap(Assets.buttons, 90, 425, 50, 150, 51, 51); // rotate button
        g.drawPixmap(Assets.buttons, 160, 425, 0, 150, 51, 51); // down button
    }

    private void drawRunningUI() {
        Log.i(LOG_TAG, "drawRunningUI -- begin");
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.buttons, 5, 20, 50, 100, 51, 51); // pause button
        g.drawPixmap(Assets.buttons, 20, 425, 50, 50, 51, 51);  // left button
        g.drawPixmap(Assets.buttons, 230, 425, 0, 50, 51, 51); // right button
        g.drawPixmap(Assets.buttons, 90, 425, 50, 150, 51, 51); // rotate button
        g.drawPixmap(Assets.buttons, 160, 425, 0, 150, 51, 51); // down button
    }

    private void drawPausedUI() {
        Log.i(LOG_TAG, "drawPausedUI -- begin");
        Graphics g = game.getGraphics();
        g.drawPixmap(Assets.pausemenu, 100, 100);
    }

    private void drawGameOverUI() {
        Log.i(LOG_TAG, "drawGameOverUI -- begin");
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.gameoverscreen, 0, 0);
        g.drawPixmap(Assets.buttons, 128, 200, 0, 100, 51, 51);
        drawText(g, ""+ DroidsWorld.getInstance().getScore(), 180, 280);
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

            int srcX = 0;
            int srcWidth = 0;
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
            Settings.save(game.getFileIO());
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}