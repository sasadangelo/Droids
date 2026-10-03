package org.code4projects.framework.transition

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TileWipeTransitionTest {
    @Test
    fun everyTileIsEmptyAtTheStartAndFullAtTheEnd() {
        for (delay in listOf(0f, 0.3f, 1f)) {
            assertEquals(0f, TileWipeTransition.tileCoverage(0f, delay, 0.6f), 1e-5f)
            assertEquals(1f, TileWipeTransition.tileCoverage(1f, delay, 0.6f), 1e-5f)
        }
    }

    @Test
    fun laterTilesLagBehindEarlierOnes() {
        val early = TileWipeTransition.tileCoverage(0.5f, 0f, 0.6f)
        val late = TileWipeTransition.tileCoverage(0.5f, 1f, 0.6f)
        assertTrue(early > late)
    }

    @Test
    fun zeroSpreadAnimatesAllTilesTogether() {
        assertEquals(
            TileWipeTransition.tileCoverage(0.4f, 0f, 0f),
            TileWipeTransition.tileCoverage(0.4f, 1f, 0f),
            1e-5f
        )
    }

    @Test
    fun fullSpreadSwitchesEachTileAtItsDelay() {
        assertEquals(0f, TileWipeTransition.tileCoverage(0.49f, 0.5f, 1f), 1e-5f)
        assertEquals(1f, TileWipeTransition.tileCoverage(0.5f, 0.5f, 1f), 1e-5f)
    }
}
