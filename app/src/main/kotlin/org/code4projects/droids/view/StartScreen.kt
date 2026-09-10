/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen

/*
 * This class represents the start screen. It contains the logo and the main menu with three
 * options:
 *     Play
 *     Highscores
 *     Quit
 *
 *  It also has a button to activate/deactivate sound.
 *
 * @author Salvatore D'Angelo
 */
class StartScreen : Screen {
    private val backgroundBounds = Rectangle(0, 0, 320, 480)
    private val logoBounds = Rectangle(32, 20, 256, 160)
    private val soundButtonBounds = Rectangle(32, 370, 50, 50)
    private val mainMenuBounds = Rectangle(84, 220, 153, 124)
    private val playMenuBounds = Rectangle(64, 220, 192, 42)
    private val highscoresMenuBounds = Rectangle(64, 220 + 42, 192, 42)
    private val quitMenuBounds = Rectangle(64, 220 + 84, 192, 42)

    /*
     * Check the user input and if one the the folloing things could occurs:
     *     - Play the game
     *     - See Highscores
     *     - Quit game
     *     - Activate/deactivate sound
     */
    override fun update(deltaTime: Float) {
        val touchEvents = Gdx.input!!.getTouchEvents()

        val len = touchEvents.size
        for (i in 0 until len) {
            val event = touchEvents[i]
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                // activate/deactivate sound
                if (soundButtonBounds.contains(event.x, event.y)) {
                    Settings.soundEnabled = !Settings.soundEnabled
                    if (Settings.soundEnabled)
                        Assets.click!!.play(1f)
                }
                // play the game
                if (playMenuBounds.contains(event.x, event.y)) {
                    Gdx.game!!.setScreen(GameScreen())
                    if (Settings.soundEnabled)
                        Assets.click!!.play(1f)
                    return
                }
                // see highscores.
                if (highscoresMenuBounds.contains(event.x, event.y)) {
                    Gdx.game!!.setScreen(HighscoreScreen())
                    if (Settings.soundEnabled)
                        Assets.click!!.play(1f)
                    return
                }
                // quit the game.
                if (quitMenuBounds.contains(event.x, event.y)) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                    if (Settings.soundEnabled)
                        Assets.click!!.play(1f)
                    System.exit(1)
                    return
                }
            }
        }
    }

    /*
     * Draw the start screen.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!

        // draw the background
        g.drawPixmap(Assets.startscreen!!, backgroundBounds.x, backgroundBounds.y)
        // draw the logo
        g.drawPixmap(Assets.logo!!, logoBounds.x, logoBounds.y)
        // draw the main menu
        g.drawPixmap(Assets.mainmenu!!, mainMenuBounds.x, mainMenuBounds.y)
        // draw the sound button depending on sound status.
        if (Settings.soundEnabled)
            g.drawPixmap(
                Assets.buttons!!, soundButtonBounds.x, soundButtonBounds.y, 0, 0,
                soundButtonBounds.width + 1, soundButtonBounds.height + 1
            )
        else
            g.drawPixmap(
                Assets.buttons!!, soundButtonBounds.x, soundButtonBounds.y, 50, 0,
                soundButtonBounds.width + 1, soundButtonBounds.height + 1
            )
    }

    /*
     * The screen is paused.
     */
    override fun pause() {
        Settings.save(Gdx.fileIO!!)
    }

    /*
     * The screen is resumed.
     */
    override fun resume() {
    }

    /*
     * The screen is disposed.
     */
    override fun dispose() {
    }
}
