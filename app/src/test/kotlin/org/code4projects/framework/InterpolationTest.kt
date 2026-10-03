package org.code4projects.framework

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterpolationTest {
    private val curves = mapOf(
        "linear" to Interpolation.linear,
        "easeIn" to Interpolation.easeIn,
        "easeOut" to Interpolation.easeOut,
        "easeInOut" to Interpolation.easeInOut,
        "easeOutBack" to Interpolation.easeOutBack,
    )

    @Test
    fun everyCurveStartsAtZeroAndEndsAtOne() {
        for ((name, curve) in curves) {
            assertEquals(name, 0f, curve.apply(0f), 1e-5f)
            assertEquals(name, 1f, curve.apply(1f), 1e-5f)
        }
    }

    @Test
    fun inputsOutsideTheRangeAreClamped() {
        for ((name, curve) in curves) {
            assertEquals(name, curve.apply(0f), curve.apply(-3f), 1e-5f)
            assertEquals(name, curve.apply(1f), curve.apply(7f), 1e-5f)
        }
    }

    @Test
    fun easeInIsSlowerThanLinearAtTheStartAndEaseOutFaster() {
        assertTrue(Interpolation.easeIn.apply(0.25f) < 0.25f)
        assertTrue(Interpolation.easeOut.apply(0.25f) > 0.25f)
        assertEquals(0.5f, Interpolation.easeInOut.apply(0.5f), 1e-5f)
    }

    @Test
    fun easeOutBackOvershootsBeforeSettling() {
        assertTrue(Interpolation.easeOutBack.apply(0.7f) > 1f)
    }

    @Test
    fun rangeVersionMapsOntoFromTo() {
        assertEquals(10f, Interpolation.linear.apply(10f, 20f, 0f), 1e-5f)
        assertEquals(15f, Interpolation.linear.apply(10f, 20f, 0.5f), 1e-5f)
        assertEquals(20f, Interpolation.linear.apply(10f, 20f, 1f), 1e-5f)
    }
}
