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

    // To help user the game shows also a queue of upcoming shapes that will fall after the
    // current one completes its falling. Index 0 is the very next shape to fall.
    private val nextShapesQueue: MutableList<Shape> = ArrayList()
    val nextShapes: List<Shape>
        get() = nextShapesQueue

    // The shape currently stashed via hold, if any.
    var heldShape: Shape? = null
        private set

    // Whether hold can be used again for the shape that is currently falling - true right after
    // a new shape starts falling, false once hold has already been used for it, so the player
    // can't cycle the same piece in and out of hold indefinitely to stall for a better draw.
    private var canHold = true

    // the remaining number of lines to fill to complete the current level
    var goal: Int = 0
        private set

    // the user score
    var score: Int = 0

    init {
        repeat(NEXT_QUEUE_SIZE) { enqueueNextShape() }
        makeNextShapeFalling()
        goal = 5
    }

    companion object {
        // The droids world is a grid of 10x20 cells
        const val WORLD_WIDTH = 10
        const val WORLD_HEIGHT = 20

        // How many upcoming shapes are shown/kept queued ahead of the falling one. Capped at 2
        // (the low end of the roadmap's suggested 2-3): the "Next" preview column only has
        // room, before it runs into the Score panel below it, for two worst-case-height (the
        // 4-block-tall I piece) shapes stacked without overlapping - confirmed by testing 3
        // on-device, where the third slot's shape visibly collided with the Score label.
        const val NEXT_QUEUE_SIZE = 2

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

    // Adds one new random shape to the end of the upcoming-shapes queue.
    private fun enqueueNextShape() {
        val r = Random()
        val index = r.nextInt(7)

        val shapes = arrayOf<Shape>(ShapeI(), ShapeL(), ShapeJ(), ShapeCube(), ShapeZ(), ShapeT(), ShapeS())
        nextShapesQueue.add(shapes[index])
    }

    // When the a shape finished to fall a new one must fall.
    // This function is used to generate a new shape that will be the
    // new falling shape.
    fun makeNextShapeFalling() {
        // Add the current falling shape's blocks to the "static" blocks list
        val falling = fallingShape
        if (falling != null) {
            // The blocks of the falling shape will be added
            // to the list of block present on the main window.
            for (block in falling.getBlocks()) {
                blockList.add(block)
            }
        }

        // The next falling shape is dequeued from the front, and a new random one is queued up
        // at the back to keep the preview showing NEXT_QUEUE_SIZE shapes ahead.
        fallingShape = nextShapesQueue.removeAt(0)
        enqueueNextShape()
        canHold = true
    }

    // Stashes the falling shape into hold, once per falling shape. If hold was empty, the next
    // queued shape becomes the new falling shape; otherwise the two swap. Either way the shape
    // going into hold is reset to its spawn position/orientation so it doesn't carry over
    // wherever the player had moved or rotated it.
    fun holdFallingShape() {
        if (!canHold) return
        val falling = fallingShape ?: return

        falling.resetSpawn()
        val previousHeld = heldShape
        heldShape = falling
        fallingShape = previousHeld ?: nextShapesQueue.removeAt(0).also { enqueueNextShape() }
        canHold = false
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
        heldShape = null
        level = 0
        score = 0
        nextShapesQueue.clear()
        repeat(NEXT_QUEUE_SIZE) { enqueueNextShape() }
        makeNextShapeFalling()
        state = GameState.Ready
        goal = 5
    }
}
