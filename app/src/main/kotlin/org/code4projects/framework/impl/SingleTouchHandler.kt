/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.view.MotionEvent
import android.view.View

import org.code4projects.framework.Input.TouchEvent
import org.code4projects.framework.Pool
import org.code4projects.framework.Pool.PoolObjectFactory

/*
 * This is the handler used to manage touch events on older Android versions.
 *
 * @author mzechner
 */
class SingleTouchHandler(view: View, private val scaleX: Float, private val scaleY: Float) : TouchHandler {
    private var isTouched = false
    private var touchX = 0
    private var touchY = 0
    private val touchEventPool: Pool<TouchEvent>
    private val touchEvents: MutableList<TouchEvent> = ArrayList()
    private val touchEventsBuffer: MutableList<TouchEvent> = ArrayList()

    init {
        val factory = object : PoolObjectFactory<TouchEvent> {
            override fun createObject(): TouchEvent = TouchEvent()
        }
        touchEventPool = Pool(factory, 100)
        view.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        synchronized(this) {
            val touchEvent = touchEventPool.newObject()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchEvent.type = TouchEvent.TOUCH_DOWN
                    isTouched = true
                }
                MotionEvent.ACTION_MOVE -> {
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED
                    isTouched = true
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    touchEvent.type = TouchEvent.TOUCH_UP
                    isTouched = false
                }
            }

            touchX = (event.x * scaleX).toInt()
            touchEvent.x = touchX
            touchY = (event.y * scaleY).toInt()
            touchEvent.y = touchY
            touchEventsBuffer.add(touchEvent)

            return true
        }
    }

    override fun isTouchDown(pointer: Int): Boolean {
        synchronized(this) {
            return pointer == 0 && isTouched
        }
    }

    override fun getTouchX(pointer: Int): Int {
        synchronized(this) {
            return touchX
        }
    }

    override fun getTouchY(pointer: Int): Int {
        synchronized(this) {
            return touchY
        }
    }

    override fun getTouchEvents(): List<TouchEvent> {
        synchronized(this) {
            val len = touchEvents.size
            for (i in 0 until len)
                touchEventPool.free(touchEvents[i])
            touchEvents.clear()
            touchEvents.addAll(touchEventsBuffer)
            touchEventsBuffer.clear()
            return touchEvents
        }
    }
}
