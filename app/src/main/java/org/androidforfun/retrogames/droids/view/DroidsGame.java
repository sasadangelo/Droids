package org.androidforfun.retrogames.droids.view;

import org.androidforfun.framework.Screen;
import org.androidforfun.framework.impl.AndroidGame;

/*
 DroidsGame

 This class represents the main activity of the Droids game.
 */
public class DroidsGame extends AndroidGame {
    private static DroidsGame instance = null;

    public static DroidsGame getInstance() {
        if (instance == null) {
            instance = new DroidsGame();
        }
        return instance;
    }

    @Override
    public Screen getStartScreen() {
        return new LoadingScreen();
    }
}