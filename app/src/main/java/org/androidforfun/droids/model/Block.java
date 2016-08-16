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
package org.androidforfun.droids.model;

import org.androidforfun.framework.Actor;

/*
 * This class represents a simple block in DroidsWorld game.
 * All the pieces in the game (i.e. L Shape, Z Shape, Cube Shape and so on)
 * are composed by blocks. For example, this is an example of L Shape where
 * each X is a block:
 *
 *    X
 *    X
 *    X
 *    XX
 *
 * DroidsWorld is a grid where each cells is a block. Logically a block has a 1x1
 * cells. Physically, on a video windows, a block has its upper left corner in
 * x,y and its size is width and height that is fixed to 20x20 pixels.
 */
public class Block extends Actor {
    // true if the block is falling
    private boolean falling;

    private int color;

    public Block() {
        // The initial position of the block is 0, 0 and its size is
        //# 20x20 pixel. The default color is white.
        super(0, 0, 1, 1);
        color = 0xffffffff;
    }

    // Check if this block collides with other block. The game objects has
    // a list of all the block currently present on video windows, so it is
    // sufficient scan them and check if one them collide with current block.
    // The method returns the first block found that collide with this one, nil
    // otherwise.
    public boolean collide() {
        for (Block block : DroidsWorld.getInstance().getBlocks()) {
            if (collide(block)) {
                return true;
            }
        }
        if (x > DroidsWorld.WORLD_WIDTH-1 || y > DroidsWorld.WORLD_HEIGHT-1 ||
                x < 0 || y < 0)
            return true;
        return false;
    }

    // When user move a shape, each block of the shape must be moved.
    // The followings methods are used to move a block.
    public void moveDown() {
        moveBy(0, 1);
    }
    public void moveUp() {
        moveBy(0, -1);
    }
    public void moveLeft() {
        moveBy(-1, 0);
    }
    public void moveRight() {
        moveBy(1, 0);
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
}
