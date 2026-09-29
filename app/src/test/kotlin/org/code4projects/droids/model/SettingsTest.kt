package org.code4projects.droids.model

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class SettingsTest {
    @Test
    fun addScoreKeepsHighScoresOrdered() {
        val original = Settings.highscores.copyOf()
        try {
            Settings.highscores = intArrayOf(100, 80, 50, 30, 10)

            Settings.addScore(75)

            assertArrayEquals(intArrayOf(100, 80, 75, 50, 30), Settings.highscores)
        } finally {
            Settings.highscores = original
        }
    }
}
