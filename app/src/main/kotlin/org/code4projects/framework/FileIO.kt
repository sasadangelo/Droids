/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Provides standard access to the filesystem, classpath, Android SD card, and Android assets
 * directory.
 *
 * @author mzechner
 */
interface FileIO {
    /**
     * Read the asset specified by fileName and return the input stream.
     */
    @Throws(IOException::class)
    fun readAsset(fileName: String): InputStream

    /**
     * Read the file, on external storage, specified by fileName and return the input stream.
     */
    @Throws(IOException::class)
    fun readFile(fileName: String): InputStream

    /**
     * Write the file, on external storage, specified by fileName and return the input stream.
     */
    @Throws(IOException::class)
    fun writeFile(fileName: String): OutputStream
}
