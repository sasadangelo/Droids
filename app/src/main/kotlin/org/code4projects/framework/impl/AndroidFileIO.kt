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

import android.content.res.AssetManager
import android.os.Environment

import org.code4projects.framework.FileIO

/*
 * This class implements the FileIO interface used to manage assets and files. The implementation is
 * based on Java classes FileInputStream and FileOutputStream and Android class AssetManager.
 *
 * @author mzechner
 * @author Salvatore D'Angelo
 */
class AndroidFileIO(private val assets: AssetManager) : FileIO {
    private val externalStoragePath: String =
        Environment.getExternalStorageDirectory().absolutePath + File.separator

    override fun readAsset(fileName: String): InputStream = assets.open(fileName)

    override fun readFile(fileName: String): InputStream = FileInputStream(externalStoragePath + fileName)

    override fun writeFile(fileName: String): OutputStream = FileOutputStream(externalStoragePath + fileName)
}
