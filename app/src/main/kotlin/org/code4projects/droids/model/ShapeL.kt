/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

/*
 * This is a L shape.
 */
class ShapeL : Shape(2, 3) {
    // An L-Shape has 4 rotation cycle, so it needs
    // one rotation to complete a cycle.
    init {
        rotationBlock = blockArray[1]
        rotationCycle = 4
    }

    // The x,y position of each block is adjusted according to
    // the shape position and size. The adjust is first applied
    // assuming the shape is its first position. Then it is rotated.
    // The color is orange.
    override fun getBlocks(): Array<Block> {
        blockArray[0].x = x
        blockArray[0].y = y
        blockArray[1].x = x
        blockArray[1].y = blockArray[0].y + 1
        blockArray[2].x = x
        blockArray[2].y = blockArray[1].y + 1
        blockArray[3].x = x + 1
        blockArray[3].y = blockArray[2].y

        applyRotation()

        for (block in blockArray) {
            block.color = 0xffff7f00L.toInt()
        }
        return blockArray
    }
}
