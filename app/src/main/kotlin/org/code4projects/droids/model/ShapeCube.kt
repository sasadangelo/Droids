/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

/*
 * This is a Cube shape.
 */
class ShapeCube : Shape(2, 2) {
    // The x,y position of each block is adjusted according to
    // the shape position and size. The color of the shape is yellow.
    override fun getBlocks(): Array<Block> {
        blockArray[0].x = x
        blockArray[1].x = x
        blockArray[2].x = x + 1
        blockArray[3].x = x + 1
        blockArray[0].y = y
        blockArray[1].y = blockArray[0].y + 1
        blockArray[2].y = blockArray[0].y
        blockArray[3].y = blockArray[2].y + 1

        for (block in blockArray) {
            block.color = 0xffffff00L.toInt()
        }
        return blockArray
    }
}
