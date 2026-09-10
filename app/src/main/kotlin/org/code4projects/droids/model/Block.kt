/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

import org.code4projects.framework.Actor

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
class Block : Actor(0, 0, 1, 1) {
    // true if the block is falling
    private var falling = false

    // The default color is white.
    var color: Int = 0xffffffffL.toInt()

    // Check if this block collides with other block. The game objects has
    // a list of all the block currently present on video windows, so it is
    // sufficient scan them and check if one them collide with current block.
    // The method returns true if a collision is found, false otherwise.
    override fun collide(): Boolean {
        for (block in DroidsWorld.getInstance().blocks) {
            if (collide(block)) {
                return true
            }
        }
        if (x > DroidsWorld.WORLD_WIDTH - 1 || y > DroidsWorld.WORLD_HEIGHT - 1 ||
            x < 0 || y < 0
        ) {
            return true
        }
        return false
    }

    // When user move a shape, each block of the shape must be moved.
    // The followings methods are used to move a block.
    fun moveDown() = moveBy(0, 1)
    fun moveUp() = moveBy(0, -1)
    fun moveLeft() = moveBy(-1, 0)
    fun moveRight() = moveBy(1, 0)
}
