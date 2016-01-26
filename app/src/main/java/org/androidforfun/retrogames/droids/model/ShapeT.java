package org.androidforfun.retrogames.droids.model;

import org.androidforfun.retrogames.droids.model.Block;
import org.androidforfun.retrogames.droids.model.Shape;

/*
 This is a T shape.
 */
public class ShapeT extends Shape {
    // An I-Shape has 2 rotation cycle, so it needs
    // to rotation to complete a cycle.
    public ShapeT() {
        super();
        rotation_block = blocks[1];
        rotation_cycle = 4;
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color is magenta.
    public Block[] getBlocks() {
        blocks[0].setX(x);
        blocks[1].setX(x + 1);
        blocks[2].setX(x + 2);
        blocks[3].setX(x + 1);
        blocks[0].setY(y);
        blocks[1].setY(y);
        blocks[2].setY(y);
        blocks[3].setY(y + 1);

        apply_rotation();
        for (Block block : blocks) {
            block.setColor(0xffff00ff);
        }
        return blocks;
    }
}
