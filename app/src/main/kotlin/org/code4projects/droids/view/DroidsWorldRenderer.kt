/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.droids.model.ShapeCube
import org.code4projects.droids.model.ShapeI
import org.code4projects.droids.model.ShapeJ
import org.code4projects.droids.model.ShapeL
import org.code4projects.droids.model.ShapeS
import org.code4projects.droids.model.ShapeT
import org.code4projects.droids.model.ShapeZ
import org.code4projects.framework.Gdx

/*
 * The responsibility of this class is to draw the model representation of Droids world.
 *
 * @author Salvatore D'Angelo
 */
class DroidsWorldRenderer {
    companion object {
        const val BLOCK_WIDTH = 40
        const val BLOCK_HEIGHT = 40

        // The "Next" preview blocks are drawn smaller than their native 32px size, both to look
        // a little less chunky and to leave more room for a visible gap between queued shapes.
        private const val NEXT_BLOCK_SRC_SIZE = 32
        const val NEXT_BLOCK_SIZE = 24
        private const val NEXT_BLOCK_SCALE = NEXT_BLOCK_SIZE.toFloat() / NEXT_BLOCK_SRC_SIZE

        // Vertical space reserved per upcoming shape in the "Next" preview queue. Must clear the
        // tallest shape (the 4-block-tall I piece, 4 * NEXT_BLOCK_SIZE = 96px) plus a visible
        // gap, so consecutive shapes never look like they're touching/merging.
        const val NEXT_QUEUE_SLOT_HEIGHT = 120
    }

    /*
     This method draw the model representation of Droids world.
     */
    fun draw(gameScreen: GameScreen) {
        /*
         * First we draw all the blocks laying on the bottom of the game screen.
         */
        for (block in DroidsWorld.getInstance().blocks) {
            val x = gameScreen.workingRegion.x + block.x * BLOCK_WIDTH
            val y = gameScreen.workingRegion.y + block.y * BLOCK_HEIGHT
            Gdx.graphics!!.drawPixmap(Assets.getBlockByColor(block.color)!!, x, y)
        }

        val fallingShape = DroidsWorld.getInstance().fallingShape!!

        /*
         * Draw a ghost preview of where the falling shape will land, using the same
         * collision logic (Shape.dropDistance()) the real fall relies on. Skipped when the
         * shape is already resting, since the ghost would then sit exactly under it.
         */
        val ghostDrop = fallingShape.dropDistance()
        if (ghostDrop > 0) {
            for (block in fallingShape.getBlocks()) {
                val x = gameScreen.workingRegion.x + block.x * BLOCK_WIDTH
                val y = gameScreen.workingRegion.y + (block.y + ghostDrop) * BLOCK_HEIGHT
                val ghostColor = (block.color and 0x00ffffff) or 0x50000000
                Gdx.graphics!!.drawRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT, ghostColor)
            }
        }

        /*
         * Draw the 4 blocks of the falling shape.
         */
        for (block in fallingShape.getBlocks()) {
            val x = gameScreen.workingRegion.x + block.x * BLOCK_WIDTH
            val y = gameScreen.workingRegion.y + block.y * BLOCK_HEIGHT
            Gdx.graphics!!.drawPixmap(Assets.getBlockByColor(block.color)!!, x, y)
        }

        // Draw the upcoming shapes queue in the Game Screen on the top right side, stacked
        // vertically with the very next shape to fall on top. Positions/offsets are computed at
        // the assets' native 32px block size, then scaled down together so each shape stays
        // centered the same way it was before, just smaller.
        for ((index, nextShape) in DroidsWorld.getInstance().nextShapes.withIndex()) {
            for (block in nextShape.getBlocks()) {
                var x = block.x * NEXT_BLOCK_SRC_SIZE
                val y = block.y * NEXT_BLOCK_SRC_SIZE

                when (nextShape) {
                    is ShapeCube, is ShapeJ -> x += 30
                    is ShapeI -> x += 50
                    is ShapeL -> x += 40
                    is ShapeS, is ShapeT, is ShapeZ -> x += 10
                }

                Gdx.graphics!!.drawPixmap(
                    Assets.getSmallBlockByColor(block.color)!!,
                    gameScreen.rightRegion.x + (x * NEXT_BLOCK_SCALE).toInt(),
                    gameScreen.rightRegion.y + 130 + index * NEXT_QUEUE_SLOT_HEIGHT +
                        (y * NEXT_BLOCK_SCALE).toInt(),
                    0, 0, NEXT_BLOCK_SRC_SIZE, NEXT_BLOCK_SRC_SIZE, NEXT_BLOCK_SIZE, NEXT_BLOCK_SIZE
                )
            }
        }
    }
}
