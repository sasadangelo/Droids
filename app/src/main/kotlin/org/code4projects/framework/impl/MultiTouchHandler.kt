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
 * This is the handler used to manage touch events on newer Android versions.
 *
 * @author mzechner
 */
class MultiTouchHandler(view: View, private val scaleX: Float, private val scaleY: Float) : TouchHandler {
    private val isTouched = BooleanArray(20)
    private val touchX = IntArray(20)
    private val touchY = IntArray(20)
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
            val action = event.action and MotionEvent.ACTION_MASK
            var pointerIndex =
                (event.action and MotionEvent.ACTION_POINTER_ID_MASK) shr MotionEvent.ACTION_POINTER_ID_SHIFT
            var pointerId = event.getPointerId(pointerIndex)
            var touchEvent: TouchEvent

            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    touchEvent = touchEventPool.newObject()
                    touchEvent.type = TouchEvent.TOUCH_DOWN
                    touchEvent.pointer = pointerId
                    touchX[pointerId] = (event.getX(pointerIndex) * scaleX).toInt()
                    touchEvent.x = touchX[pointerId]
                    touchY[pointerId] = (event.getY(pointerIndex) * scaleY).toInt()
                    touchEvent.y = touchY[pointerId]
                    isTouched[pointerId] = true
                    touchEventsBuffer.add(touchEvent)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                    touchEvent = touchEventPool.newObject()
                    touchEvent.type = TouchEvent.TOUCH_UP
                    touchEvent.pointer = pointerId
                    touchX[pointerId] = (event.getX(pointerIndex) * scaleX).toInt()
                    touchEvent.x = touchX[pointerId]
                    touchY[pointerId] = (event.getY(pointerIndex) * scaleY).toInt()
                    touchEvent.y = touchY[pointerId]
                    isTouched[pointerId] = false
                    touchEventsBuffer.add(touchEvent)
                }

                MotionEvent.ACTION_MOVE -> {
                    val pointerCount = event.pointerCount
                    for (i in 0 until pointerCount) {
                        pointerIndex = i
                        pointerId = event.getPointerId(pointerIndex)

                        touchEvent = touchEventPool.newObject()
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED
                        touchEvent.pointer = pointerId
                        touchX[pointerId] = (event.getX(pointerIndex) * scaleX).toInt()
                        touchEvent.x = touchX[pointerId]
                        touchY[pointerId] = (event.getY(pointerIndex) * scaleY).toInt()
                        touchEvent.y = touchY[pointerId]
                        touchEventsBuffer.add(touchEvent)
                    }
                }
            }

            return true
        }
    }

    override fun isTouchDown(pointer: Int): Boolean {
        synchronized(this) {
            return !(pointer < 0 || pointer >= 20) && isTouched[pointer]
        }
    }

    override fun getTouchX(pointer: Int): Int {
        synchronized(this) {
            return if (pointer < 0 || pointer >= 20) 0 else touchX[pointer]
        }
    }

    override fun getTouchY(pointer: Int): Int {
        synchronized(this) {
            return if (pointer < 0 || pointer >= 20) 0 else touchY[pointer]
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
