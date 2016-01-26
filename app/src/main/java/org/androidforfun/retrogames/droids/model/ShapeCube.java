package org.androidforfun.retrogames.droids.model;

import org.androidforfun.retrogames.droids.model.Block;
import org.androidforfun.retrogames.droids.model.Shape;

/*
 This is a Cube shape.
 */
public class ShapeCube extends Shape {
    public ShapeCube() {
        super();
    }
    // The x,y position of each block is adjusted according to
    // the shape position and size. The color of the shape is yellow.
    public Block[] getBlocks() {
        blocks[0].setX(x);
        blocks[1].setX(x);
        blocks[2].setX(x + 1);
        blocks[3].setX(x + 1);
        blocks[0].setY(y);
        blocks[1].setY(blocks[0].getY() + 1);
        blocks[2].setY(blocks[0].getY());
        blocks[3].setY(blocks[2].getY() + 1);

        for (Block block : blocks) {
            block.setColor(0xffffff00);
        }
        return blocks;
    }
}
