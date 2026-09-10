/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.content.Context
import android.view.View

import org.code4projects.framework.Input

/*
 * This class implements the Input subsystem for Android. On Android inputs come from:
 *     accelerometer
 *     keyboard
 *     touch screen
 *
 * Each subsystem is managed by a specific handler (AccelerometerHandler, KeyboardHandler and
 * SingleTouchHandler/MultiTouchHandler). For touch screen events modern hardware are managed by
 * MultiTouchHandler class. Very old hardware are managed by SingleTouchHandler.
 *
 * @author mzechner
 */
class AndroidInput(context: Context, view: View, scaleX: Float, scaleY: Float) : Input {
    private val accelHandler = AccelerometerHandler(context)
    private val keyHandler = KeyboardHandler(view)
    private val touchHandler: TouchHandler =
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5)
            SingleTouchHandler(view, scaleX, scaleY)
        else
            MultiTouchHandler(view, scaleX, scaleY)

    override fun isKeyPressed(keyCode: Int): Boolean = keyHandler.isKeyPressed(keyCode)

    override fun isTouchDown(pointer: Int): Boolean = touchHandler.isTouchDown(pointer)

    override fun getTouchX(pointer: Int): Int = touchHandler.getTouchX(pointer)

    override fun getTouchY(pointer: Int): Int = touchHandler.getTouchY(pointer)

    override fun getAccelX(): Float = accelHandler.getAccelX()

    override fun getAccelY(): Float = accelHandler.getAccelY()

    override fun getAccelZ(): Float = accelHandler.getAccelZ()

    override fun getTouchEvents(): List<Input.TouchEvent> = touchHandler.getTouchEvents()

    override fun getKeyEvents(): List<Input.KeyEvent> = keyHandler.getKeyEvents()
}
