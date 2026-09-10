/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * Interface to the input facilities. This allows polling the state of the keyboard, the touch
 * screen and the accelerometer.
 *
 * @author mzechner
 */
interface Input {
    /**
     * This class represents a event from keyboard. Each event from keyboard could be of two type:
     * KEY_DOWN or KEY_UP; depending on the pressing of releasing of a key. Whenever a keyboard
     * event is generated the relative key code (keyCode) and its human readable representation
     * (keyChar) is associated to the event.
     *
     * @author mzechner
     */
    class KeyEvent {
        @JvmField
        var type = 0

        @JvmField
        var keyCode = 0

        @JvmField
        var keyChar = '\u0000'

        /**
         * Returns an human readable representation of the key event.
         */
        override fun toString(): String {
            val builder = StringBuilder()
            if (type == KEY_DOWN)
                builder.append("key down, ")
            else
                builder.append("key up, ")
            builder.append(keyCode)
            builder.append(",")
            builder.append(keyChar)
            return builder.toString()
        }

        companion object {
            const val KEY_DOWN = 0
            const val KEY_UP = 1
        }
    }

    /**
     * This class represents a event generated when user touch the video. A finger can touch
     * (TOUCH_DOWN), release (TOUCH_UP) or drag (TOUCH_DRAGGED) on the screen.
     * When the event is generated it will be associated to the event also the (x, y) position
     * touched by the finger. When N fingers touch the video, N event will be generated and they
     * differ by the pointer value. For the first finger point=1, the second pointer=2 and so on.
     *
     * @author mzechner
     */
    class TouchEvent {
        @JvmField
        var type = 0

        @JvmField
        var x = 0

        @JvmField
        var y = 0

        @JvmField
        var pointer = 0

        /**
         * Returns an human readable representation of the touch event.
         */
        override fun toString(): String {
            val builder = StringBuilder()
            if (type == TOUCH_DOWN)
                builder.append("touch down, ")
            else if (type == TOUCH_DRAGGED)
                builder.append("touch dragged, ")
            else
                builder.append("touch up, ")
            builder.append(pointer)
            builder.append(",")
            builder.append(x)
            builder.append(",")
            builder.append(y)
            return builder.toString()
        }

        companion object {
            const val TOUCH_DOWN = 0
            const val TOUCH_UP = 1
            const val TOUCH_DRAGGED = 2
        }
    }

    /**
     * Returns whether the key is pressed.
     */
    fun isKeyPressed(keyCode: Int): Boolean

    /**
     * Whether the screen is currently touched by the pointer with the given index. Pointers are
     * indexed from 0 to n. The pointer id identifies the order in which the fingers went down on
     * the screen, e.g. 0 is the first finger, 1 is the second and so on. When two fingers are
     * touched down and the first one is lifted the second one keeps its index. If another finger
     * is placed on the touch screen the first free index will be used.
     */
    fun isTouchDown(pointer: Int): Boolean

    /**
     * Returns the x coordinate in screen coordinates touched by the finger identified by the input
     * pointer.
     */
    fun getTouchX(pointer: Int): Int

    /**
     * Returns the y coordinate in screen coordinates touched by the finger identified by the input
     * pointer.
     */
    fun getTouchY(pointer: Int): Int

    /**
     * @return the rate of rotation around the x axis. (rad/s)
     */
    fun getAccelX(): Float

    /**
     * @return the rate of rotation around the y axis. (rad/s)
     */
    fun getAccelY(): Float

    /**
     * @return the rate of rotation around the z axis. (rad/s)
     */
    fun getAccelZ(): Float

    /**
     * @return the list of key events on the event queue.
     */
    fun getKeyEvents(): List<KeyEvent>

    /**
     * @return the list of touch events on the event queue.
     */
    fun getTouchEvents(): List<TouchEvent>
}
