/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

import android.os.SystemClock
import org.code4projects.framework.Actor

/*
 * Shape
 *
 * It is the base class for all the DroidsWorld shapes (I-Shape, L-Shape, Z-Shape, Cube-Shape and so on).
 */
abstract class Shape protected constructor(width: Int, height: Int) : Actor(0, 0, width, height) {
    // The blocks belonging to a shape. Each shape is composed by 4 blocks.
    protected var blockArray: Array<Block> = arrayOf(Block(), Block(), Block(), Block())

    // true if the shape is falling. At each moment only one shape at a time is falling.
    var falling = true

    // when the user positioned correctly the falling shape he can press the down button to
    // accelerate its falling. In this way for each cell it earn a soft point. If the shape
    // pass N cells in accelerated way the user earn N points. This is called "soft score" that will
    // be added to the user total score.
    private var accelerating = false

    var softDropScore = 0
        private set

    // each shape has a block around which the shape rotate.
    // The rotation variable indicates how many rotation the shape must perform.
    // The rotationCycle indicate, for each shape, how many rotation are necessary to have it
    // at the same position.
    protected var rotationBlock: Block = blockArray[1]
    protected var rotationCycle = 1
    protected var rotation = 0

    // these variable are used to synch up movements and avoid that game run too fast on high
    // performance systems.
    private var lastFallUpdate: Long = SystemClock.uptimeMillis()
    private var lastMoveUpdate: Long = SystemClock.uptimeMillis()

    // This is the method that apply the rotation on the shape.
    fun applyRotation() {
        for (i in 1..(rotation % rotationCycle)) {
            for (block in blockArray) {
                val oldX = block.x
                val oldY = block.y
                block.x = rotationBlock.x + (rotationBlock.y - oldY)
                block.y = rotationBlock.y - (rotationBlock.x - oldX)
            }
        }
    }

    fun rotate() {
        rotation += 1
    }

    fun undoRotate() {
        rotation -= 1
    }

    // This method returns true if the time passed from last fall movement of the shape
    // is > than updateInterval. The updateInterval decrease with increase of game level and when
    // user press down key.
    fun needsFallUpdate(): Boolean {
        // updateInterval is 450 for level 1
        //                   400 for level 2
        //                   350 for level 3
        //             ...
        //                   100 for level 8
        // It is 50 if user press down key.
        var updateInterval = (500 - DroidsWorld.getInstance().level * 50).toLong()
        if (accelerating) {
            updateInterval = 50L
        }

        // If the time passed from last update is > updateInterval then
        // an update is necessary and lastFallUpdate is updated to current time.
        if (SystemClock.uptimeMillis() - lastFallUpdate > updateInterval) {
            lastFallUpdate = SystemClock.uptimeMillis()
            return true
        }
        return false
    }

    // This method returns true if the time passed from last horizontal movement of the shape
    // is > than 100 ms.
    fun needsMoveUpdate(): Boolean {
        if (SystemClock.uptimeMillis() - lastMoveUpdate > 100) {
            lastMoveUpdate = SystemClock.uptimeMillis()
            return true
        }
        return false
    }

    // This method is used to update the shape when it falls
    fun update() {
        // The update occurs only if the shape is falling. If it lays on other shapes
        // nothing happen
        if (falling) {
            // Check if it is passed enough time to let the shape falls.
            // In this case y is increased by 1 cell.
            if (needsFallUpdate()) {
                moveDown()
            }

            // We let the shape falls but if it collides the following occurs:
            // 1. the shape returns to the old position
            // 2. the falling process completes
            // 3. the game engine let a new shape to fall
            // 4. a check is done to verify if a line has been completed and then can be removed.
            if (collide()) {
                moveUp()
                falling = false
            }
        }
    }

    // This method checks if the shape collide with something.
    // The method first scan all the 4 blocks to check if they collide
    // with another block in the game.
    override fun collide(): Boolean {
        for (block in getBlocks()) {
            if (block.collide()) {
                return true
            }
        }
        return false
    }

    fun moveDown() {
        for (block in blockArray) {
            block.moveDown()
        }
        moveBy(0, 1)
        if (accelerating) {
            softDropScore++
        }
    }

    fun moveUp() {
        for (block in blockArray) {
            block.moveUp()
        }
        moveBy(0, -1)
        if (accelerating) {
            softDropScore--
        }
    }

    fun moveLeft() {
        for (block in blockArray) {
            block.moveLeft()
        }
        moveBy(-1, 0)
    }

    fun moveRight() {
        for (block in blockArray) {
            block.moveRight()
        }
        moveBy(1, 0)
    }

    abstract fun getBlocks(): Array<Block>

    fun accelerateFalling() {
        accelerating = true
    }
}
