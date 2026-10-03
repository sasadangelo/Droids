/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import kotlin.math.sin

import org.code4projects.droids.model.DroidsWorld
import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input
import org.code4projects.framework.Pixmap
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen

/*
 * This class represents the start (home) screen: the "DROIDS" block logo over the blue
 * background, a big Play button (Resume when a game is paused or running) and a row of tiles
 * along the bottom for the high scores, the settings and quitting. Positions follow the visible
 * area so the screen fills tall phones too.
 *
 * @author Salvatore D'Angelo
 */
class StartScreen : Screen {
    companion object {
        private const val TILE_WIDTH = 150
        private const val TILE_HEIGHT = 130
        private const val TILE_GAP = 20
        private const val BOTTOM_MARGIN = 48
        private const val ICON_SIZE = 72
    }

    private val playButtonBounds: Rectangle
    private val logoCenterY: Float
    private val highscoresTile: Rectangle
    private val settingsTile: Rectangle
    private val quitTile: Rectangle

    private val playStyle = DroidsUi.textStyle(64, DroidsUi.VALUE_COLOR)
    private val tileStyle = DroidsUi.textStyle(24, DroidsUi.TITLE_COLOR)

    // Drives the logo bob and the Play button pulse.
    private var time = 0f

    init {
        val g = Gdx.graphics!!
        val top = g.getVisibleTop()
        val height = g.getVisibleBottom() - top
        val width = g.getWidth()

        logoCenterY = top + height * 0.27f

        val button = Assets.playButton!!
        playButtonBounds = Rectangle(
            (width - button.getWidth()) / 2, top + (height * 0.58f).toInt() - button.getHeight() / 2,
            button.getWidth(), button.getHeight()
        )

        val tilesY = g.getVisibleBottom() - BOTTOM_MARGIN - TILE_HEIGHT
        var x = (width - 3 * TILE_WIDTH - 2 * TILE_GAP) / 2
        highscoresTile = Rectangle(x, tilesY, TILE_WIDTH, TILE_HEIGHT)
        x += TILE_WIDTH + TILE_GAP
        settingsTile = Rectangle(x, tilesY, TILE_WIDTH, TILE_HEIGHT)
        x += TILE_WIDTH + TILE_GAP
        quitTile = Rectangle(x, tilesY, TILE_WIDTH, TILE_HEIGHT)
    }

    private fun isGameInProgress(): Boolean {
        val state = DroidsWorld.getInstance().state
        return state == DroidsWorld.GameState.Paused || state == DroidsWorld.GameState.Running
    }

    /*
     * Check the user input and if one the following things could occur:
     *     - Play the game (choosing a mode first, unless a game is already paused/running)
     *     - See Highscores
     *     - Open the settings screen
     *     - Quit game
     */
    override fun update(deltaTime: Float) {
        time += deltaTime
        val touchEvents = Gdx.input!!.getTouchEvents()

        for (event in touchEvents) {
            if (event.type != Input.TouchEvent.TOUCH_UP) continue

            // play the game: resume directly if a game is already paused/running (so pausing and
            // going home doesn't lose progress), otherwise let the player choose a mode first.
            if (playButtonBounds.contains(event.x, event.y)) {
                Assets.playClick()
                if (isGameInProgress()) Transitions.play(this, GameScreen())
                else Transitions.forward(this, ModeSelectScreen())
                return
            }
            if (highscoresTile.contains(event.x, event.y)) {
                Assets.playClick()
                Transitions.forward(this, HighscoreScreen())
                return
            }
            if (settingsTile.contains(event.x, event.y)) {
                Assets.playClick()
                Transitions.forward(this, SettingsScreen())
                return
            }
            // quit the game, after confirmation.
            if (quitTile.contains(event.x, event.y)) {
                Assets.playClick()
                Gdx.game!!.confirmExit { exitGame() }
                return
            }
        }
    }

    /*
     * Draw the start screen.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!

        g.drawBackground(Assets.splashBackground!!)

        // the logo floats gently, like at the end of the splash screen
        val bob = sin(time * 2f) * 6f
        g.drawPixmap(Assets.splashLogo!!, g.getWidth() / 2f, logoCenterY + bob, 1f, 0f, 1f)

        // the Play button pulses slightly to invite a tap
        val pulse = 1f + sin(time * 3f) * 0.025f
        val centerX = playButtonBounds.x + playButtonBounds.width / 2f
        val centerY = playButtonBounds.y + playButtonBounds.height / 2f
        g.save()
        g.scale(pulse, pulse, centerX, centerY)
        g.drawPixmap(Assets.playButton!!, playButtonBounds.x, playButtonBounds.y)
        val label = if (isGameInProgress()) "RESUME" else "PLAY"
        g.drawText(label, centerX.toInt(), (centerY + playStyle.textSize * 0.32f).toInt(), playStyle)
        g.restore()

        drawTile(g, highscoresTile, Assets.iconTrophy!!, "SCORES")
        drawTile(g, settingsTile, Assets.iconGear!!, "SETTINGS")
        drawTile(g, quitTile, Assets.iconPower!!, "QUIT")
    }

    private fun drawTile(g: Graphics, tile: Rectangle, icon: Pixmap, label: String) {
        DroidsUi.drawPanel(g, tile)
        g.drawPixmap(icon, tile.x + (tile.width - ICON_SIZE) / 2, tile.y + 14)
        g.drawText(label, tile.x + tile.width / 2, tile.y + tile.height - 18, tileStyle)
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
     * Ask for confirmation before exiting, same as the quit tile.
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
