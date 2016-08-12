package org.androidforfun.droids.model;

/*
 This is a Z shape.
 */
public class ShapeZ extends Shape {
    // An L-Shape has 4 rotation cycle, so it needs
    // to rotation to complete a cycle.
    public ShapeZ() {
        super(3, 2);
        rotation_block = blocks[1];
        rotation_cycle = 2;
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color is red.
    public Block[] getBlocks() {
        blocks[0].setX(x);
        blocks[0].setY(y);
        blocks[1].setX(x + 1);
        blocks[1].setY(y);
        blocks[2].setX(x + 1);
        blocks[2].setY(y + 1);
        blocks[3].setX(x + 2);
        blocks[3].setY(y + 1);

        apply_rotation();
        for (Block block : blocks) {
            block.setColor(0xffff0000);
        }
        return blocks;
    }
}
