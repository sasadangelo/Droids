/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen
import org.code4projects.framework.impl.FadeTransitionScreen

/*
 * This class represents the start screen. It contains the logo and the main menu with three
 * options:
 *     Play
 *     Highscores
 *     Quit
 *
 *  It also has a button that opens the settings screen (music/SFX toggles and volume).
 *
 * @author Salvatore D'Angelo
 */
class StartScreen : Screen {
    private val backgroundBounds = Rectangle(0, 0, 640, 960)
    private val logoBounds = Rectangle(64, 40, 512, 320)
    private val settingsButtonBounds = Rectangle(64, 740, 100, 100)
    private val mainMenuBounds = Rectangle(168, 440, 306, 248)
    private val playMenuBounds = Rectangle(128, 440, 384, 84)
    private val highscoresMenuBounds = Rectangle(128, 440 + 84, 384, 84)
    private val quitMenuBounds = Rectangle(128, 440 + 168, 384, 84)

    /*
     * Check the user input and if one the the folloing things could occurs:
     *     - Play the game (choosing a mode first, unless a game is already paused/running)
     *     - See Highscores
     *     - Quit game
     *     - Open the settings screen
     */
    override fun update(deltaTime: Float) {
        val touchEvents = Gdx.input!!.getTouchEvents()

        val len = touchEvents.size
        for (i in 0 until len) {
            val event = touchEvents[i]
            if (event.type == Input.TouchEvent.TOUCH_UP) {
                // open the settings screen
                if (settingsButtonBounds.contains(event.x, event.y)) {
                    Assets.playClick()
                    Gdx.game!!.setScreen(FadeTransitionScreen(this, SettingsScreen()))
                    return
                }
                // play the game: resume directly if a game is already paused/running (so
                // pausing and going home doesn't lose progress), otherwise let the player
                // choose a mode for a new game.
                if (playMenuBounds.contains(event.x, event.y)) {
                    Assets.playClick()
                    val world = DroidsWorld.getInstance()
                    val resuming = world.state == DroidsWorld.GameState.Paused ||
                        world.state == DroidsWorld.GameState.Running
                    val nextScreen = if (resuming) GameScreen() else ModeSelectScreen()
                    Gdx.game!!.setScreen(FadeTransitionScreen(this, nextScreen))
                    return
                }
                // see highscores.
                if (highscoresMenuBounds.contains(event.x, event.y)) {
                    Gdx.game!!.setScreen(FadeTransitionScreen(this, HighscoreScreen()))
                    Assets.playClick()
                    return
                }
                // quit the game, after confirmation.
                if (quitMenuBounds.contains(event.x, event.y)) {
                    Assets.playClick()
                    Gdx.game!!.confirmExit { exitGame() }
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
        // draw the settings button; the icon reflects whether any audio is currently enabled.
        if (Settings.musicEnabled || Settings.sfxEnabled)
            g.drawPixmap(
                Assets.buttons!!, settingsButtonBounds.x, settingsButtonBounds.y, 0, 0,
                settingsButtonBounds.width + 1, settingsButtonBounds.height + 1
            )
        else
            g.drawPixmap(
                Assets.buttons!!, settingsButtonBounds.x, settingsButtonBounds.y, 100, 0,
                settingsButtonBounds.width + 1, settingsButtonBounds.height + 1
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

    /*
     * Ask for confirmation before exiting, same as the quit menu item.
     */
    override fun backPressed(): Boolean {
        Gdx.game!!.confirmExit { exitGame() }
        return true
    }

    /*
     * Terminates the app. Called once the user has confirmed they want to quit.
     */
    private fun exitGame() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }
}
