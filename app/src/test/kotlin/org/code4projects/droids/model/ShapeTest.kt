package org.code4projects.droids.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShapeTest {
    @Test
    fun cubeHasFourBlocksAtExpectedCoordinates() {
        val shape = ShapeCube()
        shape.setPosition(3, 4)

        val coordinates = shape.getBlocks().map { it.x to it.y }

        assertEquals(listOf(3 to 4, 3 to 5, 4 to 4, 4 to 5), coordinates)
    }

    @Test
    fun iShapeReturnsToItsOriginalOrientationAfterTwoRotations() {
        val shape = ShapeI()
        shape.setPosition(3, 4)
        val original = shape.getBlocks().map { it.x to it.y }

        shape.rotate()
        val rotated = shape.getBlocks().map { it.x to it.y }
        shape.rotate()
        val restored = shape.getBlocks().map { it.x to it.y }

        assertEquals(listOf(4 to 5, 3 to 5, 2 to 5, 1 to 5), rotated)
        assertEquals(original, restored)
    }

    @Test
    fun fourCycleShapesReturnToTheirOriginalOrientation() {
        val shapes = listOf(ShapeL(), ShapeT())

        for (shape in shapes) {
            shape.setPosition(4, 4)
            val original = shape.getBlocks().map { it.x to it.y }

            repeat(4) { shape.rotate() }

            assertEquals(original, shape.getBlocks().map { it.x to it.y })
        }
    }

    @Test
    fun dropDistanceStopsAtTheBottomOfTheWorld() {
        val world = DroidsWorld.getInstance()
        world.clear()
        val shape = ShapeCube()
        shape.setPosition(0, 0)
        shape.getBlocks()

        assertEquals(DroidsWorld.WORLD_HEIGHT - shape.height, shape.dropDistance())
        assertTrue(world.blocks.isEmpty())
    }
}
