/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

import android.content.Context
import android.content.res.AssetManager

import org.code4projects.framework.FileIO

/*
 * This class implements the FileIO interface used to manage assets and files. The implementation is
 * based on Java classes FileInputStream and FileOutputStream and Android class AssetManager.
 *
 * Settings/highscores are stored under the app's private files directory (context.filesDir)
 * rather than external/shared storage, since scoped storage (Android 10+/API 29+) blocks
 * unrestricted external storage access for apps targeting a modern SDK - the app's own files
 * directory needs no permission on any supported API level.
 *
 * @author mzechner
 * @author Salvatore D'Angelo
 */
class AndroidFileIO(context: Context, private val assets: AssetManager) : FileIO {
    private val filesPath: String = context.filesDir.absolutePath + File.separator

    override fun readAsset(fileName: String): InputStream = assets.open(fileName)

    override fun readFile(fileName: String): InputStream = FileInputStream(filesPath + fileName)

    override fun writeFile(fileName: String): OutputStream = FileOutputStream(filesPath + fileName)
}
