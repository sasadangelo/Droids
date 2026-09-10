/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

/*
 * This is a S shape.
 */
class ShapeS : Shape(3, 2) {
    init {
        rotationBlock = blockArray[1]
        rotationCycle = 2
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color is green.
    override fun getBlocks(): Array<Block> {
        blockArray[0].x = x + 2
        blockArray[0].y = y
        blockArray[1].x = x + 1
        blockArray[1].y = y
        blockArray[2].x = x + 1
        blockArray[2].y = y + 1
        blockArray[3].x = x
        blockArray[3].y = y + 1

        applyRotation()

        for (block in blockArray) {
            block.color = 0xff00ff00L.toInt()
        }
        return blockArray
    }
}
