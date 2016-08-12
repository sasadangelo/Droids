package org.androidforfun.droids.view;

import org.androidforfun.framework.Screen;
import org.androidforfun.framework.impl.AndroidGame;

/*
 DroidsGame

 This class represents the main activity of the Droids game.
 */
public class DroidsGame extends AndroidGame {
    @Override
    public Screen getStartScreen() {
        return new LoadingScreen();
    }
}