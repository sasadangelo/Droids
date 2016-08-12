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

public class DroidsWorldRenderer {
    public static int BLOCK_WIDTH=20;
    public static int BLOCK_HEIGHT=20;

    public void draw() {
        GameScreen gameScreen = (GameScreen) Gdx.game.getCurrentScreen();

        for (Block block : DroidsWorld.getInstance().getBlocks()) {
            int x = gameScreen.getWorkingRegion().getX() + block.getX()*BLOCK_WIDTH;
            int y = gameScreen.getWorkingRegion().getY() + block.getY()*BLOCK_HEIGHT;
            Gdx.graphics.drawPixmap(Assets.getBlockByColor(block.getColor()), x, y);
        }

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
