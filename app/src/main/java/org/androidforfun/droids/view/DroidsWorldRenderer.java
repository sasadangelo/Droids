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

import org.androidforfun.droids.model.Block;
import org.androidforfun.droids.model.DroidsWorld;
import org.androidforfun.droids.model.ShapeCube;
import org.androidforfun.droids.model.ShapeI;
import org.androidforfun.droids.model.ShapeJ;
import org.androidforfun.droids.model.ShapeL;
import org.androidforfun.droids.model.ShapeS;
import org.androidforfun.droids.model.ShapeT;
import org.androidforfun.droids.model.ShapeZ;
import org.androidforfun.framework.Gdx;

/*
 * The responsibility of this class is to draw the model representation of Droids world.
 *
 * @author Salvatore D'Angelo
 */
public class DroidsWorldRenderer {
    public static int BLOCK_WIDTH=20;
    public static int BLOCK_HEIGHT=20;




    /*
     This method draw the model representation of Droids world.
     */
    public void draw() {
        GameScreen gameScreen = (GameScreen) Gdx.game.getCurrentScreen();

        /*
         * First we draw all the blocks laying on the bottom of the game screen.
         */
        for (Block block : DroidsWorld.getInstance().getBlocks()) {
            int x = gameScreen.getWorkingRegion().getX() + block.getX()*BLOCK_WIDTH;
            int y = gameScreen.getWorkingRegion().getY() + block.getY()*BLOCK_HEIGHT;
            Gdx.graphics.drawPixmap(Assets.getBlockByColor(block.getColor()), x, y);
        }

        /*
         * Draw the 4 blocks of the falling shape.
         */
        for (Block block : DroidsWorld.getInstance().getFallingShape().getBlocks()) {
            int x = gameScreen.getWorkingRegion().getX() + block.getX()*BLOCK_WIDTH;
            int y = gameScreen.getWorkingRegion().getY() + block.getY()*BLOCK_HEIGHT;
            Gdx.graphics.drawPixmap(Assets.getBlockByColor(block.getColor()), x, y);
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

            Gdx.graphics.drawPixmap(Assets.getSmallBlockByColor(block.getColor()), gameScreen.getRightRegion().getX() + x, gameScreen.getRightRegion().getY() + 65 + y);
        }
    }
}
