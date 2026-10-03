/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.droids.model.Shape
import org.code4projects.framework.Gdx
import org.code4projects.framework.Rectangle

/*
 * The responsibility of this class is to draw the model representation of Droids world: the
 * board, the settled and falling blocks, the ghost piece and the Hold/Next previews, all sized
 * from the GameLayout.
 *
 * @author Salvatore D'Angelo
 */
class DroidsWorldRenderer(private val layout: GameLayout) {
    companion object {
        // Native size of the <color>block.png sprites, scaled (filtered) to whatever size a
        // block is drawn at.
        private const val BLOCK_SPRITE_SIZE = 64f

        // Native cell size of playfield.png, scaled the same way as the blocks.
        private const val PLAYFIELD_CELL = 64f

        // Ghost piece outline, relative to the cell size so it matches the blocks at any size.
        private const val GHOST_INSET = 0.05f
        private const val GHOST_RADIUS = 0.18f
        private const val GHOST_STROKE = 0.07f

        // Hold/Next preview block sizes, and the room left at the top of their panels for the
        // panel title.
        private const val PREVIEW_CELL = 22
        private const val SECOND_NEXT_CELL = 13
        private const val PANEL_TITLE_HEIGHT = 34
    }

    /*
     This method draw the model representation of Droids world.
     */
    fun draw() {
        val g = Gdx.graphics!!
        val board = layout.board
        val cell = layout.cell
        val world = DroidsWorld.getInstance()

        // the board (the image includes a margin around the cells for the frame and its glow)
        g.drawPixmap(
            Assets.playfield!!, board.x + board.width / 2f, board.y + board.height / 2f,
            cell / PLAYFIELD_CELL, 0f, 1f
        )

        // the blocks laying on the bottom of the board
        for (block in world.blocks) {
            drawBlock(block.color, board.x + block.x * cell, board.y + block.y * cell, cell)
        }

        val fallingShape = world.fallingShape!!

        /*
         * Draw a ghost preview of where the falling shape will land, using the same
         * collision logic (Shape.dropDistance()) the real fall relies on. Skipped when the
         * shape is already resting, since the ghost would then sit exactly under it.
         */
        val ghostDrop = fallingShape.dropDistance()
        if (ghostDrop > 0) {
            val inset = (cell * GHOST_INSET).toInt()
            val size = cell - 2 * inset
            val radius = cell * GHOST_RADIUS
            val stroke = maxOf(2f, cell * GHOST_STROKE)
            for (block in fallingShape.getBlocks()) {
                val x = board.x + block.x * cell + inset
                val y = board.y + (block.y + ghostDrop) * cell + inset
                val tint = Assets.getBlockTint(block.color) and 0x00ffffff
                // a faint light veil with an outline in the piece's own color
                g.drawRoundRect(x, y, size, size, radius, 0x1effffff)
                g.drawRoundRectOutline(x, y, size, size, radius, stroke, tint or 0xb4000000.toInt())
            }
        }

        // the 4 blocks of the falling shape
        for (block in fallingShape.getBlocks()) {
            drawBlock(block.color, board.x + block.x * cell, board.y + block.y * cell, cell)
        }

        // Next panel: the very next shape on the left, the one after it smaller on the right.
        val next = layout.nextPanel
        val previewTop = next.y + PANEL_TITLE_HEIGHT
        val previewHeight = next.height - PANEL_TITLE_HEIGHT - 8
        val split = next.width * 64 / 100
        world.nextShapes.getOrNull(0)?.let {
            drawShapePreview(it, Rectangle(next.x + 4, previewTop, split - 4, previewHeight), PREVIEW_CELL)
        }
        world.nextShapes.getOrNull(1)?.let {
            drawShapePreview(
                it, Rectangle(next.x + split, previewTop, next.width - split - 6, previewHeight), SECOND_NEXT_CELL
            )
        }

        // Hold panel: the held shape, if any.
        world.heldShape?.let {
            val hold = layout.holdPanel
            drawShapePreview(
                it, Rectangle(hold.x, hold.y + PANEL_TITLE_HEIGHT, hold.width, hold.height - PANEL_TITLE_HEIGHT - 8),
                PREVIEW_CELL
            )
        }
    }

    /*
     * Draws one glossy block with its top-left corner at (x, y), size x size.
     */
    private fun drawBlock(color: Int, x: Int, y: Int, size: Int) {
        Gdx.graphics!!.drawPixmap(
            Assets.getBlockByColor(color)!!, x + size / 2f, y + size / 2f, size / BLOCK_SPRITE_SIZE, 0f, 1f
        )
    }

    /*
     * Draws a shape with blocks of the given size, centered in the box whatever its position and
     * rotation in the model, by centering the bounding box of its blocks.
     */
    private fun drawShapePreview(shape: Shape, box: Rectangle, blockSize: Int) {
        val blocks = shape.getBlocks()
        val minX = blocks.minOf { it.x }
        val minY = blocks.minOf { it.y }
        val width = (blocks.maxOf { it.x } - minX + 1) * blockSize
        val height = (blocks.maxOf { it.y } - minY + 1) * blockSize
        val originX = box.x + (box.width - width) / 2
        val originY = box.y + (box.height - height) / 2
        for (block in blocks) {
            drawBlock(block.color, originX + (block.x - minX) * blockSize, originY + (block.y - minY) * blockSize, blockSize)
        }
    }
}
