/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * An `Game` is the main entry point of your project. It sets up a window and rendering
 * surface and manages the different aspects of your application, namely [Graphics],
 * [Audio], [Input] and [FileIO]. Think of a Game being equivalent to Swing's
 * `JFrame` or Android's `Activity`.
 *
 * The Application interface provides you with a set of modules for graphics, audio, input and file i/o.
 *
 * [Graphics] offers you various methods to output visuals to the screen.
 *
 * [Audio] offers you various methods to output and record sound and music. This is achieved
 * via the Android media framework.
 *
 * [Input] offers you various methods to poll user input from the keyboard, touch screen,
 * mouse and accelerometer.
 *
 * [FileIO] offers you various methods to access internal and external files. An internal file
 * is a file that is stored near your application. On Android internal files are equivalent to
 * assets. On Android external files reside on the SD-card. If you know what you are doing you can
 * also specify absolute file names. Absolute filenames are not portable, so take great care when
 * using this feature.
 *
 * The `Game` also has a set of methods that you can use to query specific information
 * such as the operating system the application is currently running on and so forth. This allows
 * you to have operating system dependent code paths. It is however not recommended to use this
 * facilities.
 *
 * @author mzechner
 */
interface Game {
    /**
     * @return the [Input] instance
     */
    fun getInput(): Input

    /**
     * @return the [FileIO] instance
     */
    fun getFileIO(): FileIO

    /**
     * @return the [Graphics] instance
     */
    fun getGraphics(): Graphics

    /**
     * @return the [Audio] instance
     */
    fun getAudio(): Audio

    /**
     * Dispose the current screen and set the input screen as new current screen.
     */
    fun setScreen(screen: Screen)

    /**
     * @return the current screen.
     */
    fun getCurrentScreen(): Screen

    /**
     * Set the first screen of the game. This is usually implemented by the source code of the game.
     */
    fun getStartScreen(): Screen
}
