/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.model

import org.code4projects.framework.FileIO

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/*
 * Manage scores on the filesystem.
 */
object Settings {
    @JvmField
    var soundEnabled = true

    @JvmField
    var highscores = intArrayOf(100, 80, 50, 30, 10)

    @JvmStatic
    fun load(files: FileIO) {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(files.readFile(".droids")))
            soundEnabled = reader.readLine()?.toBoolean() ?: false
            for (i in 0 until 5) {
                highscores[i] = reader.readLine()!!.toInt()
            }
        } catch (e: Exception) {
            // :/ It's ok, defaults save our day
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun save(files: FileIO) {
        var writer: BufferedWriter? = null
        try {
            writer = BufferedWriter(OutputStreamWriter(files.writeFile(".droids")))
            writer.write(soundEnabled.toString())
            writer.write("\n")
            for (i in 0 until 5) {
                writer.write(highscores[i].toString())
                writer.write("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                writer?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun addScore(score: Int) {
        for (i in 0 until 5) {
            if (highscores[i] < score) {
                for (j in 4 downTo i + 1) {
                    highscores[j] = highscores[j - 1]
                }
                highscores[i] = score
                break
            }
        }
    }
}
