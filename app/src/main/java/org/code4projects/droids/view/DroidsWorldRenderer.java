/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view;

import org.code4projects.droids.model.Block;
import org.code4projects.droids.model.DroidsWorld;
import org.code4projects.droids.model.ShapeCube;
import org.code4projects.droids.model.ShapeI;
import org.code4projects.droids.model.ShapeJ;
import org.code4projects.droids.model.ShapeL;
import org.code4projects.droids.model.ShapeS;
import org.code4projects.droids.model.ShapeT;
import org.code4projects.droids.model.ShapeZ;
import org.code4projects.framework.Gdx;

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
