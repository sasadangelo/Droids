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

/*
 * The options screen: music and SFX each get a panel with an on/off toggle and a drag-to-set
 * volume slider, all drawn in code.
 *
 * @author Salvatore D'Angelo
 */
class SettingsScreen : Screen {
    companion object {
        private const val PANEL_WIDTH = 520
        private const val PANEL_HEIGHT = 200
        private const val PANEL_GAP = 32
        private const val PADDING = 28
        private const val TOGGLE_WIDTH = 130
        private const val TOGGLE_HEIGHT = 60
        private const val TRACK_HEIGHT = 18
        private const val KNOB_SIZE = 44
        // Extra vertical room around a slider's track that still grabs it.
        private const val SLIDER_TOUCH_SLOP = 30

        private const val TRACK_COLOR = 0xff1a2470.toInt()
        private const val FILL_COLOR = 0xff5fd0ff.toInt()
        private const val KNOB_COLOR = 0xffffffff.toInt()
        private const val TOGGLE_ON_COLOR = 0xff3cc62a.toInt()
        private const val TOGGLE_OFF_COLOR = 0xffd8384f.toInt()
    }

    private enum class Slider { MUSIC, SFX }

    private class Row(val panel: Rectangle) {
        val toggle = Rectangle(
            panel.x + panel.width - PADDING - TOGGLE_WIDTH, panel.y + PADDING, TOGGLE_WIDTH, TOGGLE_HEIGHT
        )
        val track = Rectangle(
            panel.x + PADDING + KNOB_SIZE / 2, panel.y + panel.height - PADDING - KNOB_SIZE / 2 - TRACK_HEIGHT / 2,
            panel.width - 2 * PADDING - KNOB_SIZE, TRACK_HEIGHT
        )
        val sliderHitArea = Rectangle(
            panel.x, track.y - SLIDER_TOUCH_SLOP, panel.width, track.height + 2 * SLIDER_TOUCH_SLOP
        )
    }

    private val page = MenuPage("SETTINGS")
    private val music: Row
    private val sfx: Row

    // Which slider, if any, is currently being dragged - set on TOUCH_DOWN inside its hit area,
    // cleared on TOUCH_UP, so a single drag doesn't affect both sliders and a tap elsewhere on
    // the screen doesn't move a slider it didn't start on.
    private var activeSlider: Slider? = null

    private val labelStyle = DroidsUi.textStyle(40, DroidsUi.VALUE_COLOR).apply { align = TextStyle.Align.LEFT }
    private val toggleStyle = DroidsUi.textStyle(28, DroidsUi.VALUE_COLOR)

    init {
        val total = 2 * PANEL_HEIGHT + PANEL_GAP
        val x = (Gdx.graphics!!.getWidth() - PANEL_WIDTH) / 2
        val y = page.contentTop + (page.contentBottom - page.contentTop - total) / 2
        music = Row(Rectangle(x, y, PANEL_WIDTH, PANEL_HEIGHT))
        sfx = Row(Rectangle(x, y + PANEL_HEIGHT + PANEL_GAP, PANEL_WIDTH, PANEL_HEIGHT))
    }

    /*
     * Check the user input: dragging a slider updates the corresponding volume live, tapping a
     * toggle flips it, tapping back returns to the start screen.
     */
    override fun update(deltaTime: Float) {
        for (event in Gdx.input!!.getTouchEvents()) {
            when (event.type) {
                TouchEvent.TOUCH_DOWN -> {
                    if (music.sliderHitArea.contains(event.x, event.y)) {
                        activeSlider = Slider.MUSIC
                        applySlider(Slider.MUSIC, event.x)
                    } else if (sfx.sliderHitArea.contains(event.x, event.y)) {
                        activeSlider = Slider.SFX
                        applySlider(Slider.SFX, event.x)
                    }
                }
                TouchEvent.TOUCH_DRAGGED -> {
                    activeSlider?.let { applySlider(it, event.x) }
                }
                TouchEvent.TOUCH_UP -> {
                    activeSlider = null
                    if (page.backButton.contains(event.x, event.y)) {
                        Assets.playClick()
                        Transitions.back(this, StartScreen())
                        return
                    }
                    if (music.toggle.contains(event.x, event.y)) {
                        Settings.musicEnabled = !Settings.musicEnabled
                        Assets.updateMusicVolume()
                        Assets.playClick()
                    }
                    if (sfx.toggle.contains(event.x, event.y)) {
                        Settings.sfxEnabled = !Settings.sfxEnabled
                        Assets.playClick()
                    }
                }
            }
        }
    }

    // Maps a touch x position onto the given slider's 0f..1f volume range and applies it.
    private fun applySlider(slider: Slider, touchX: Int) {
        val track = if (slider == Slider.MUSIC) music.track else sfx.track
        val fraction = ((touchX - track.x).toFloat() / track.width).coerceIn(0f, 1f)
        if (slider == Slider.MUSIC) {
            Settings.musicVolume = fraction
            Assets.updateMusicVolume()
        } else {
            Settings.sfxVolume = fraction
        }
    }

    /*
     * Draw the settings screen: a Music panel and an SFX panel, each with a toggle and a volume
     * slider.
     */
    override fun draw(deltaTime: Float) {
        val g: Graphics = Gdx.graphics!!
        page.draw(g)
        drawRow(g, music, "MUSIC", Settings.musicEnabled, Settings.musicVolume)
        drawRow(g, sfx, "SFX", Settings.sfxEnabled, Settings.sfxVolume)
    }

    private fun drawRow(g: Graphics, row: Row, label: String, enabled: Boolean, volume: Float) {
        DroidsUi.drawPanel(g, row.panel)
        g.drawText(label, row.panel.x + PADDING, row.toggle.y + row.toggle.height / 2 + 14, labelStyle)

        // toggle: a pill, green when on, red when off
        val toggle = row.toggle
        g.drawRoundRect(
            toggle.x, toggle.y, toggle.width, toggle.height, toggle.height / 2f,
            if (enabled) TOGGLE_ON_COLOR else TOGGLE_OFF_COLOR
        )
        g.drawRoundRectOutline(toggle.x, toggle.y, toggle.width, toggle.height, toggle.height / 2f, 3f, KNOB_COLOR)
        g.drawText(
            if (enabled) "ON" else "OFF", toggle.x + toggle.width / 2, toggle.y + toggle.height / 2 + 10, toggleStyle
        )

        // slider: rounded track, filled up to the volume, round knob
        val track = row.track
        val radius = track.height / 2f
        g.drawRoundRect(track.x, track.y, track.width, track.height, radius, TRACK_COLOR)
        val filled = (track.width * volume.coerceIn(0f, 1f)).toInt()
        if (filled > 0) {
            g.drawRoundRect(track.x, track.y, filled, track.height, radius, FILL_COLOR)
        }
        val knobX = track.x + filled - KNOB_SIZE / 2
        val knobY = track.y + track.height / 2 - KNOB_SIZE / 2
        g.drawRoundRect(knobX, knobY + 3, KNOB_SIZE, KNOB_SIZE, KNOB_SIZE / 2f, DroidsUi.SHADOW_COLOR)
        g.drawRoundRect(knobX, knobY, KNOB_SIZE, KNOB_SIZE, KNOB_SIZE / 2f, KNOB_COLOR)
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
        Transitions.back(this, StartScreen())
        return true
    }
}
