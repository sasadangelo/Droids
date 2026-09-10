/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

import java.util.Random

/*
 * This is the main model class. It is the entry point that describe the DroidsWorld world.
 */
class DroidsWorld private constructor() {

    // the possible game status values
    enum class GameState {
        Ready,
        Running,
        Paused,
        GameOver
    }

    // the game status
    var state: GameState = GameState.Ready

    // this is the list of blocks laying in the bottom of screen.
    private val blockList: MutableList<Block> = ArrayList()

    // the game level
    var level: Int = 0
        private set

    // In droids falling shape.. This variable contains the falling shape.
    var fallingShape: Shape? = null
        private set

    // To help user the game shows also the next shape that will fall after the current one completed
    // its falling.
    var nextShape: Shape? = null
        private set

    // the remaining number of lines to fill to complete the current level
    var goal: Int = 0
        private set

    // the user score
    var score: Int = 0

    init {
        spawnNextShape()
        makeNextShapeFalling()
        goal = 5
    }

    companion object {
        // The droids world is a grid of 10x20 cells
        const val WORLD_WIDTH = 10
        const val WORLD_HEIGHT = 20

        // the private static instance used to implement the Singleton pattern.
        private var instance: DroidsWorld? = null

        @JvmStatic
        fun getInstance(): DroidsWorld {
            if (instance == null) {
                instance = DroidsWorld()
            }
            return instance!!
        }
    }

    // When the a shape finished to fall a new one must fall.
    // This function is used to generate a new shape that will be the
    // new falling shape.
    fun spawnNextShape() {
        val r = Random()
        val index = r.nextInt(7)

        val shapes = arrayOf<Shape>(ShapeI(), ShapeL(), ShapeJ(), ShapeCube(), ShapeZ(), ShapeT(), ShapeS())
        nextShape = shapes[index]
    }

    // When the a shape finished to fall a new one must fall.
    // This function is used to generate a new shape that will be the
    // new falling shape.
    fun makeNextShapeFalling() {
        // Spawn a random shape and add the current falling shape' blocks to the "static" blocks list
        val falling = fallingShape
        if (falling != null) {
            // The blocks of the falling shape will be added
            // to the list of block present on the main window.
            for (block in falling.getBlocks()) {
                blockList.add(block)
            }
        }

        fallingShape = nextShape
        spawnNextShape()
    }

    fun update(deltaTime: Float) {
        if (state == GameState.GameOver) return

        if (state == GameState.Running) {
            val falling = fallingShape!!
            if (falling.collide()) {
                state = GameState.GameOver
            } else {
                falling.update()
                if (!falling.falling) {
                    val layingShape = falling
                    score += layingShape.softDropScore
                    makeNextShapeFalling()
                    deleteLinesOf(layingShape)
                }
            }
        }
    }

    // When the falling shape completes its journey this method is called
    // to check if its block completes one or more lines.
    private fun deleteLinesOf(shape: Shape) {
        val deletedLines = mutableListOf<Int>()

        // Go through each block of the shape and check if the lines they are on are complete.
        // If so, the line of the block will be candidate for removal.
        for (shapeBlock in shape.getBlocks()) {
            if (lineComplete(shapeBlock.y)) {
                deletedLines.add(shapeBlock.y)
                // Remove from blocks all the block belonging to the same line.
                val iterator = blockList.iterator()
                while (iterator.hasNext()) {
                    val block = iterator.next()
                    if (block.y == shapeBlock.y) iterator.remove()
                }
            }
        }

        // goal is decreased by the number of lines deleted.
        goal -= deletedLines.size
        when (deletedLines.size) {
            1 -> score += 40 * (level + 1)
            2 -> score += 100 * (level + 1)
            3 -> score += 300 * (level + 1)
            4 -> score += 1200 * (level + 1)
        }
        if (goal <= 0) {
            ++level
            goal += 5 * level + 5
        }

        // This applies the standard gravity found in classic DroidsWorld games - all blocks go down by the
        // amount of lines cleared
        for (block in blockList) {
            var count = 0
            for (y in deletedLines) {
                if (y > block.y) ++count
            }
            block.y = block.y + count
        }
    }

    // Given an y line on the screen this method says if it is complete.
    // A line is complete when we found WIDTH SCREEN/WIDTH BLOCK (320/32=10 items) on the line.
    private fun lineComplete(y: Int): Boolean {
        // Important is that the screen resolution should be divisable by the block_width, otherwise there would be gap
        // If the count of blocks at a line is equal to the max possible blocks for any line - the line is complete
        var count = 0
        for (block in blockList) {
            if (block.y == y) ++count
        }
        return count == WORLD_WIDTH
    }

    val blocks: List<Block>
        get() = blockList

    fun clear() {
        blockList.clear()
        fallingShape = null
        level = 0
        score = 0
        spawnNextShape()
        makeNextShapeFalling()
        state = GameState.Ready
        goal = 5
    }
}
