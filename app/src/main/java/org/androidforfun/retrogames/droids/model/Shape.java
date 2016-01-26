package org.androidforfun.retrogames.droids.model;

import android.os.SystemClock;

import org.androidforfun.retrogames.droids.model.Block;
import org.androidforfun.retrogames.droids.model.DroidsWorld;

/*
 Shape

 It is the base class for all the DroidsWorld shapes (I-Shape, L-Shape, Z-Shape, Cube-Shape and so on).
 */
public abstract class Shape {
    // The blocks belonging to a shape. Each shape is composed by 4 blocks.
    protected Block blocks[];
    // the shape position
    protected int x, y;
    // true if the shape is falling. At each moment only one shape at a time is falling.
    protected boolean falling;
    // when the user positioned correctly the falling shape he can press the down button to
    // accelerate its falling. In this way for each cell it earn a soft point. If the shape
    // pass N cells in accelerated way the user earn N points. This is called "soft score" that will
    // be added to the user total score.
    protected boolean accelerateFalling;
    protected int softDropScore;
    // each shape has a block around which the shape rotate.
    // The rotation variable indicates how many rotation the shape must perform.
    // The rotation_cycle indicate, for each shape, how many rotation are necessary to have it
    // at the same position.
    protected Block rotation_block;
    protected int rotation_cycle, rotation;
    // these variable are used to synch up movements and avoid that game run too fast on high
    // performance systems.
    protected long last_fall_update;
    protected long last_move_update;

    public Shape() {
        // A shape can move in two way:
        // 1. horizontally when user press left/right keys
        // 2. vertically when the shape falls
        //
        // Both the movement should occurs after a specific time. This is necessary
        // to avoid to have a game with object that moves like crazy.
        // Each horizontal movement should wait at least 100 ms.
        // Each vertical movement should wait a variable time that depends on:
        // 1. the game level: in this case the updateInterval is (500 - gamelevel*50) ms
        // 2. the user press down key: in this case the time is 100 ms
        last_fall_update = SystemClock.uptimeMillis();
        last_move_update = SystemClock.uptimeMillis();

        // All the shapes in DroidsWorld are composed by 4 blocks:
        blocks = new Block[4];
        blocks[0] = new Block();
        blocks[1] = new Block();
        blocks[2] = new Block();
        blocks[3] = new Block();

        // All the shapes are positioned in upper-left corner and
        // all of thems falls at the beginning. When the shape lay
        // on another shape the falling terminates.
        x = y = 0;
        falling = true;

        // Each shape rotate around the second block (block[1]). The second
        // block represents the rotation axis.
        rotation_block = blocks[1];
        // How many rotations we can do before a full cycle? I-Shape requires
        // 2 rotations, L-Shape 4, T-Shape 4, Z-Shape 4.
        rotation_cycle = 1;
        // Current rotation state
        rotation = 0;
    }

    // This is the method that apply the rotation on the shape.
    public void apply_rotation() {
        int old_x, old_y;
        if (rotation_block != null) {
            for (int i=1; i<=(rotation % rotation_cycle); ++i) {
                for (Block block : blocks) {
                    old_x = block.getX();
                    old_y = block.getY();
                    block.setX(rotation_block.getX() + (rotation_block.getY() - old_y));
                    block.setY(rotation_block.getY() - (rotation_block.getX() - old_x));
                }
            }
        }
    }

    public void rotateRight() {
        rotation+=1;
    }

    public void rotateLeft() {
        rotation-=1;
    }

    // This method returns the minimum square that contains the shape.
    public int[] getBounds() {
        // Go throug all blocks to find the bounds of this shape
        int x_min = 0;
        int y_min = 0;
        int x_max = DroidsWorld.getInstance().WORLD_WIDTH-1;
        int y_max = DroidsWorld.getInstance().WORLD_HEIGHT-1;

        for (Block block : blocks) {
            if (block.getX() < x_min) {
                x_min = block.getX();
            }
            if (block.getY() < y_min) {
                y_min = block.getY();
            }

            if (block.getX() > x_max) {
                x_max = block.getX();
            }
            if (block.getY() > y_max) {
                y_max = block.getY();
            }
        }

        int bounds[] = {x_min, y_min, x_max, y_max};
        return bounds;
    }

    // This method returns true if the time passed from last fall movement of the shape
    // (    Gosu::milliseconds - @last_fall_update) is > than updateInterval.
    // The updateInterval decrease with increase of game level and when user
    // press down key.
    public boolean needsFallUpdate() {
        // updateInterval is 450 for level 1
        //                   400 for level 2
        //                   350 for level 3
        //             ...
        //                   100 for level 8
        // It is 100 if user press down key.
        long updateInterval = 500 - DroidsWorld.getInstance().getLevel()*50;
        if (accelerateFalling)
            //updateInterval = 100;
            updateInterval = 50;

        // If the time passed from last update is > updateInterval then
        // an update is necessary and @last_fall_update is updated to current time.
        if (SystemClock.uptimeMillis() - last_fall_update > updateInterval ) {
            last_fall_update = SystemClock.uptimeMillis();
            return true;
        }
        return false;
    }

    // This method returns true if the time passed from last horizontal movement of the shape
    // (    Gosu::milliseconds - @last_move_update) is > than 100 ms.
    public boolean needsMoveUpdate() {
        if (SystemClock.uptimeMillis() - last_move_update > 100) {
            last_move_update = SystemClock.uptimeMillis();
            return true;
        }
        return false;
    }

    // This method is used to update the shape when it falls
    public void update() {
        // The update occurs only if the shape is falling. If it lays on other shapes
        // nothing happen
        if (falling) {
            // Check if it is passed enough time to let the shape falls.
            // In this case y is increased by 20 pixels.
            if (needsFallUpdate()) {
                moveDown();
            }

            // We let the shape falls but if it collides the following occurs:
            // 1. the shape returns to the old position
            // 2. the falling process completes
            // 3. the game engine let a new shape to fall
            // 4. a check is done to verify if a line has been completed and then can be removed.
            if (collide()) {
                moveUp();
                falling = false;
            }
        }
    }

    // This method checks if the shape collide with something.
    // The method first scan all the 4 blocks to check if they collide
    // with another block in the game.
    // Then the method check also if the shape bounds collide with
    // main windows.
    public boolean collide() {
        // For each block of the shape, check if it collides with
        // another block in the game.
        for (Block block : getBlocks()) {
            if (block.collideWithOtherBlocks() != null) {
                return true;
            }
        }

        // Check if the shape collide with main windows
        int bounds[] = getBounds();

        if (bounds[3] > DroidsWorld.getInstance().WORLD_HEIGHT-1)
            return true;

        if (bounds[2] > DroidsWorld.getInstance().WORLD_WIDTH-1)
            return true;

        if (bounds[1] < 0)
            return true;

        if (bounds[0] < 0)
            return true;

        return false;
    }

    public void moveDown() {
        for (Block block : blocks) {
            block.moveDown();
        }
        y += 1;
        if (accelerateFalling) {
            softDropScore++;
        }
    }

    public void moveUp() {
        for (Block block : blocks) {
            block.moveUp();
        }
        y -= 1;
        if (accelerateFalling) {
            softDropScore--;
        }
    }

    public void moveLeft() {
        for (Block block : blocks) {
            block.moveLeft();
        }
        x -= 1;
    }

    public void moveRight() {
        for (Block block : blocks) {
            block.moveRight();
        }
        x += 1;
    }

    public abstract Block[] getBlocks();
    public boolean isFalling() {
        return falling;
    }
    public int getSoftDropScore() {
        return softDropScore;
    }
    public void accelerateFalling() {
        this.accelerateFalling = true;
    }
}
