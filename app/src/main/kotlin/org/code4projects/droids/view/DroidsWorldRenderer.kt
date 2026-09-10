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
        const val BLOCK_WIDTH = 20
        const val BLOCK_HEIGHT = 20
    }

    /*
     This method draw the model representation of Droids world.
     */
    fun draw() {
        val gameScreen = Gdx.game!!.getCurrentScreen() as GameScreen

        /*
         * First we draw all the blocks laying on the bottom of the game screen.
         */
        for (block in DroidsWorld.getInstance().blocks) {
            val x = gameScreen.workingRegion.x + block.x * BLOCK_WIDTH
            val y = gameScreen.workingRegion.y + block.y * BLOCK_HEIGHT
            Gdx.graphics!!.drawPixmap(Assets.getBlockByColor(block.color)!!, x, y)
        }

        /*
         * Draw the 4 blocks of the falling shape.
         */
        for (block in DroidsWorld.getInstance().fallingShape!!.getBlocks()) {
            val x = gameScreen.workingRegion.x + block.x * BLOCK_WIDTH
            val y = gameScreen.workingRegion.y + block.y * BLOCK_HEIGHT
            Gdx.graphics!!.drawPixmap(Assets.getBlockByColor(block.color)!!, x, y)
        }

        // This for loop draw the Next Shape in the Game Screen on the top right side
        val nextShape = DroidsWorld.getInstance().nextShape!!
        for (block in nextShape.getBlocks()) {
            var x = block.x * 16
            val y = block.y * 16

            when (nextShape) {
                is ShapeCube, is ShapeJ -> x += 15
                is ShapeI -> x += 25
                is ShapeL -> x += 20
                is ShapeS, is ShapeT, is ShapeZ -> x += 5
            }

            Gdx.graphics!!.drawPixmap(
                Assets.getSmallBlockByColor(block.color)!!,
                gameScreen.rightRegion.x + x,
                gameScreen.rightRegion.y + 65 + y
            )
        }
    }
}
