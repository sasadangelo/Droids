/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.droids.view

import org.code4projects.droids.model.Settings
import org.code4projects.framework.Gdx
import org.code4projects.framework.Graphics
import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Rectangle
import org.code4projects.framework.Screen
import org.code4projects.framework.TextStyle
import org.code4projects.framework.impl.FadeTransitionScreen

/*
 * Real options screen replacing the old single sound on/off toggle: music and SFX each get
 * their own enable toggle and a drag-to-set volume slider. There's no baked art for sliders, so
 * they're drawn from plain rectangles, the same way the ghost piece reuses drawRect instead of
 * adding new sprites.
 *
 * @author Salvatore D'Angelo
 */
class SettingsScreen : Screen {
    private enum class Slider { MUSIC, SFX }

    private val backgroundBounds = Rectangle(0, 0, 640, 960)
    private val backButtonBounds = Rectangle(64, 740, 100, 100)

    private val musicToggleBounds = Rectangle(460, 300, 120, 60)
    private val musicSliderTrack = Rectangle(80, 440, 480, 16)
    private val musicSliderHitArea = Rectangle(80, 410, 480, 76)

    private val sfxToggleBounds = Rectangle(460, 540, 120, 60)
    private val sfxSliderTrack = Rectangle(80, 680, 480, 16)
    private val sfxSliderHitArea = Rectangle(80, 650, 480, 76)

    // Which slider, if any, is currently being dragged - set on TOUCH_DOWN inside its hit area,
    // cleared on TOUCH_UP, so a single drag doesn't affect both sliders and a tap elsewhere on
    // the screen doesn't move a slider it didn't start on.
    private var activeSlider: Slider? = null

    private val titleStyle = TextStyle().apply {
        color = 0xffffffffL.toInt()
        textSize = 48
        style = TextStyle.Style.BOLD
        align = TextStyle.Align.CENTER
    }

    private val labelStyle = TextStyle().apply {
        color = 0xffffffffL.toInt()
        textSize = 32
        style = TextStyle.Style.BOLD
    }

    private val toggleStyle = TextStyle().apply {
        color = 0xffffffffL.toInt()
        textSize = 24
        style = TextStyle.Style.BOLD
        align = TextStyle.Align.CENTER
    }

    private val trackColor = 0x99202040L.toInt()
    private val fillColor = 0xff00e5ffL.toInt()
    private val knobColor = 0xffffffffL.toInt()
    private val toggleOnColor = 0x9900c853L.toInt()
    private val toggleOffColor = 0x99b71c1cL.toInt()

    /*
     * Check the user input: dragging a slider updates the corresponding volume live, tapping a
     * toggle flips it, tapping back returns to the start screen.
     */
    override fun update(deltaTime: Float) {
        val touchEvents = Gdx.input!!.getTouchEvents()
        val len = touchEvents.size
        for (i in 0 until len) {
            val event = touchEvents[i]
            when (event.type) {
                TouchEvent.TOUCH_DOWN -> {
                    if (musicSliderHitArea.contains(event.x, event.y)) {
                        activeSlider = Slider.MUSIC
                        applySlider(Slider.MUSIC, event.x)
                    } else if (sfxSliderHitArea.contains(event.x, event.y)) {
                        activeSlider = Slider.SFX
                        applySlider(Slider.SFX, event.x)
                    }
                }
                TouchEvent.TOUCH_DRAGGED -> {
                    activeSlider?.let { applySlider(it, event.x) }
                }
                TouchEvent.TOUCH_UP -> {
                    activeSlider = null
                    if (backButtonBounds.contains(event.x, event.y)) {
                        Assets.playClick()
                        Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
                        return
                    }
                    if (musicToggleBounds.contains(event.x, event.y)) {
                        Settings.musicEnabled = !Settings.musicEnabled
                        Assets.updateMusicVolume()
                        Assets.playClick()
                    }
                    if (sfxToggleBounds.contains(event.x, event.y)) {
                        Settings.sfxEnabled = !Settings.sfxEnabled
                        Assets.playClick()
                    }
                }
            }
        }
    }

    // Maps a touch x position onto the given slider's 0f..1f volume range and applies it.
    private fun applySlider(slider: Slider, touchX: Int) {
        val track = if (slider == Slider.MUSIC) musicSliderTrack else sfxSliderTrack
        val fraction = ((touchX - track.x).toFloat() / track.width).coerceIn(0f, 1f)
        if (slider == Slider.MUSIC) {
            Settings.musicVolume = fraction
            Assets.updateMusicVolume()
        } else {
            Settings.sfxVolume = fraction
        }
    }

    /*
     * Draw the settings screen: title, a Music row and an SFX row (each with a toggle and a
     * volume slider), and the back button.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!
        g.drawPixmap(Assets.startscreen!!, backgroundBounds.x, backgroundBounds.y)
        g.drawText("Settings", backgroundBounds.width / 2, 220, titleStyle)

        g.drawText("Music", musicSliderTrack.x, 280, labelStyle)
        drawToggle(g, musicToggleBounds, Settings.musicEnabled)
        drawSlider(g, musicSliderTrack, Settings.musicVolume)

        g.drawText("SFX", sfxSliderTrack.x, 520, labelStyle)
        drawToggle(g, sfxToggleBounds, Settings.sfxEnabled)
        drawSlider(g, sfxSliderTrack, Settings.sfxVolume)

        // draw the back button.
        g.drawPixmap(
            Assets.buttons!!, backButtonBounds.x, backButtonBounds.y, 100, 100,
            backButtonBounds.width + 1, backButtonBounds.height + 1
        )
    }

    private fun drawToggle(g: Graphics, bounds: Rectangle, enabled: Boolean) {
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, if (enabled) toggleOnColor else toggleOffColor)
        g.drawText(
            if (enabled) "ON" else "OFF", bounds.x + bounds.width / 2, bounds.y + bounds.height / 2 + 8, toggleStyle
        )
    }

    private fun drawSlider(g: Graphics, track: Rectangle, volume: Float) {
        g.drawRect(track.x, track.y, track.width, track.height, trackColor)
        val filledWidth = (track.width * volume.coerceIn(0f, 1f)).toInt()
        if (filledWidth > 0) {
            g.drawRect(track.x, track.y, filledWidth, track.height, fillColor)
        }
        val knobWidth = 20
        val knobX = (track.x + filledWidth - knobWidth / 2).coerceIn(track.x, track.x + track.width - knobWidth)
        g.drawRect(knobX, track.y - 12, knobWidth, track.height + 24, knobColor)
    }

    /*
     * The screen is paused: persist whatever settings the player changed.
     */
    override fun pause() {
        Settings.save(Gdx.fileIO!!)
    }

    override fun resume() {
    }

    override fun dispose() {
    }

    /*
     * Same as tapping the back button: return to the start screen.
     */
    override fun backPressed(): Boolean {
        Assets.playClick()
        Gdx.game!!.setScreen(FadeTransitionScreen(this, StartScreen()))
        return true
    }
}
