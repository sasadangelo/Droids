package org.androidforfun.retrogames.droids.model;

import org.androidforfun.retrogames.droids.model.Block;
import org.androidforfun.retrogames.droids.model.Shape;

/*
 This is a L shape.
 */
public class ShapeL extends Shape {
    // An L-Shape has 4 rotation cycle, so it needs
    // to rotation to complete a cycle.
    public ShapeL() {
        super();
        rotation_block = blocks[1];
        rotation_cycle = 4;
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color is orange.
    public Block[] getBlocks() {
        blocks[0].setX(x);
        blocks[0].setY(y);
        blocks[1].setX(x);
        blocks[1].setY(blocks[0].getY() + 1);
        blocks[2].setX(x);
        blocks[2].setY(blocks[1].getY() + 1);
        blocks[3].setX(x + 1);
        blocks[3].setY(blocks[2].getY());

        apply_rotation();

        for (Block block : blocks) {
            block.setColor(0xffff7f00);
        }
        return blocks;
    }
}
