package org.androidforfun.droids.model;

/*
 This is a I shape.
 */
public class ShapeI extends Shape {
    // An I-Shape has 2 rotation cycle, so it needs
    // to rotation to complete a cycle.
    public ShapeI() {
        super();
        rotation_block = blocks[1];
        rotation_cycle = 2;
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color of the shape is cyan.
    public Block[] getBlocks() {
        blocks[0].setX(x);
        blocks[1].setX(x);
        blocks[2].setX(x);
        blocks[3].setX(x);
        blocks[0].setY(y);
        blocks[1].setY(blocks[0].getY() + 1);
        blocks[2].setY(blocks[1].getY() + 1);
        blocks[3].setY(blocks[2].getY() + 1);

        apply_rotation();

        for (Block block : blocks) {
            block.setColor(0xffb2ffff);
        }
        return blocks;
    }
}
