package org.code4projects.droids.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test

class DroidsWorldTest {
    @Test
    fun newWorldStartsWithFallingShapeAndPreviewQueue() {
        val world = DroidsWorld.getInstance()
        world.clear()

        assertEquals(DroidsWorld.GameState.Ready, world.state)
        assertEquals(DroidsWorld.NEXT_QUEUE_SIZE, world.nextShapes.size)
        assertNotNull(world.fallingShape)
        assertEquals(5, world.goal)
    }

    @Test
    fun holdCanOnlyBeUsedOnceUntilTheNextShapeFalls() {
        val world = DroidsWorld.getInstance()
        world.clear()
        val firstFallingShape = world.fallingShape

        world.holdFallingShape()
        val heldShape = world.heldShape
        val fallingAfterFirstHold = world.fallingShape
        world.holdFallingShape()

        assertSame(firstFallingShape, heldShape)
        assertNotNull(fallingAfterFirstHold)
        assertSame(heldShape, world.heldShape)
        assertSame(fallingAfterFirstHold, world.fallingShape)

        world.makeNextShapeFalling()
        val secondFallingShape = world.fallingShape
        world.holdFallingShape()
        assertSame(secondFallingShape, world.heldShape)
    }

    @Test
    fun clearingOneLineUpdatesScoreGoalAndLineCount() {
        val world = DroidsWorld.getInstance()
        world.clear()
        world.mode = DroidsWorld.GameMode.MARATHON

        val shape = ShapeI()
        shape.setPosition(3, 18)
        shape.rotate()
        val shapeBlocks = shape.getBlocks()
        val settledBlocks = world.blocks as MutableList<Block>
        for (x in 0 until DroidsWorld.WORLD_WIDTH) {
            if (shapeBlocks.none { it.x == x && it.y == 19 }) {
                settledBlocks.add(Block().apply { setPosition(x, 19) })
            }
        }
        settledBlocks.addAll(shapeBlocks)

        invokeDeleteLines(world, shape)

        assertEquals(40, world.score)
        assertEquals(4, world.goal)
        assertEquals(1, world.linesCleared)
        assertEquals(0, world.level)
        assertEquals(0, world.blocks.size)
    }

    private fun invokeDeleteLines(world: DroidsWorld, shape: Shape) {
        val method = DroidsWorld::class.java.getDeclaredMethod("deleteLinesOf", Shape::class.java)
        method.isAccessible = true
        method.invoke(world, shape)
    }
}
