/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

/*
 * This is a I shape.
 */
class ShapeI : Shape(1, 4) {
    // An I-Shape has 2 rotation cycle, so it needs
    // one rotation to complete a cycle.
    init {
        rotationBlock = blockArray[1]
        rotationCycle = 2
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color of the shape is cyan.
    override fun getBlocks(): Array<Block> {
        blockArray[0].x = x
        blockArray[1].x = x
        blockArray[2].x = x
        blockArray[3].x = x
        blockArray[0].y = y
        blockArray[1].y = blockArray[0].y + 1
        blockArray[2].y = blockArray[1].y + 1
        blockArray[3].y = blockArray[2].y + 1

        applyRotation()

        for (block in blockArray) {
            block.color = 0xffb2ffffL.toInt()
        }
        return blockArray
    }
}
