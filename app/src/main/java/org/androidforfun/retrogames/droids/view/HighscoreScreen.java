package org.androidforfun.retrogames.droids.view;

import org.androidforfun.retrogames.droids.model.Settings;
import org.androidforfun.retrogames.framework.Game;
import org.androidforfun.retrogames.framework.Graphics;
import org.androidforfun.retrogames.framework.Input.TouchEvent;
import org.androidforfun.retrogames.framework.Screen;

import java.util.List;

public class HighscoreScreen extends Screen {
    String lines[] = new String[5];

    public HighscoreScreen(Game game) {
        super(game);

        for (int i = 0; i < 5; i++) {
            lines[i] = "" + (i + 1) + ". " + Settings.highscores[i];
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x >= 32 &&  event.x < 82 && event.y >= 370 && event.y < 430) {
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                    game.setScreen(new StartScreen(game));
                    return;
                }
            }
        }
    }

    @Override
    public void draw(float deltaTime) {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.highscoresscreen, 0, 0);
        g.drawPixmap(Assets.mainmenu, 64, 20, 0, 42, 196, 42);

        int y = 100;
        for (int i = 0; i < 5; i++) {
            drawText(g, lines[i], 20, y);
            y += 50;
        }

        g.drawPixmap(Assets.buttons, 32, 370, 50, 50, 51, 51);
    }

    public void drawText(Graphics g, String line, int x, int y) {
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

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
