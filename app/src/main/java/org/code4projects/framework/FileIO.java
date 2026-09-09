/*
 *  Copyright (C) 2016 Mario Zechner
 *  This file is part of Framework for book Beginning Android Games.
 *
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides standard access to the filesystem, classpath, Android SD card, and Android assets
 * directory.
 *
 * @author mzechner
 */
public interface FileIO {
    /**
     * Read the asset specified by fileName and return the input stream.
     * directory.
     */
    InputStream readAsset(String fileName) throws IOException;

    /**
     * Read the file, on external storage, specified by fileName and return the input stream.
     * directory.
     */
    InputStream readFile(String fileName) throws IOException;

    /**
     * Write the file, on external storage, specified by fileName and return the input stream.
     * directory.
     */
    OutputStream writeFile(String fileName) throws IOException;
}
