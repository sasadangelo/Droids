/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * Environment class holding references to the [Game], [Graphics], [Audio],
 * [FileIO] and [Input] instances. The references are held in public static fields which
 * allows static access to all sub systems. Do not use Graphics in a thread that is not the
 * rendering thread.
 *
 * This is normally a design faux pas but in this case is better than the alternatives.
 *
 * @author mzechner
 * @author Salvatore D'Angelo (to fit Gdx library)
 */
object Gdx {
    @JvmField
    var game: Game? = null

    @JvmField
    var graphics: Graphics? = null

    @JvmField
    var audio: Audio? = null

    @JvmField
    var input: Input? = null

    @JvmField
    var fileIO: FileIO? = null
}
