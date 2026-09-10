/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.view.View

import org.code4projects.framework.Input.TouchEvent

/*
 * This is the handler used to manage touch events. This class is specialized by SingleTouchHandler
 * or MultiTouchHandler depending on Android version.
 *
 * @author mzechner
 */
interface TouchHandler : View.OnTouchListener {
    fun isTouchDown(pointer: Int): Boolean
    fun getTouchX(pointer: Int): Int
    fun getTouchY(pointer: Int): Int
    fun getTouchEvents(): List<TouchEvent>
}
