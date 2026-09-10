/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.view.View

import org.code4projects.framework.Input.KeyEvent
import org.code4projects.framework.Pool
import org.code4projects.framework.Pool.PoolObjectFactory

/*
 * This is the handler used to manage keyword events. The class does not allocate a KeyEvent object
 * for each key event. It uses the Object Pool pattern to allocate 100 KeyEvent and reuse them
 * avoiding waste of memory.
 *
 * @author mzechner
 */
class KeyboardHandler(view: View) : View.OnKeyListener {
    private val pressedKeys = BooleanArray(128)
    private val keyEventPool: Pool<KeyEvent>
    private val keyEventsBuffer: MutableList<KeyEvent> = ArrayList()
    private val keyEvents: MutableList<KeyEvent> = ArrayList()

    init {
        val factory = object : PoolObjectFactory<KeyEvent> {
            override fun createObject(): KeyEvent = KeyEvent()
        }
        keyEventPool = Pool(factory, 100)
        view.setOnKeyListener(this)
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }

    override fun onKey(v: View?, keyCode: Int, event: android.view.KeyEvent): Boolean {
        if (event.action == android.view.KeyEvent.ACTION_MULTIPLE)
            return false

        synchronized(this) {
            val keyEvent = keyEventPool.newObject()
            keyEvent.keyCode = keyCode
            keyEvent.keyChar = event.unicodeChar.toChar()
            if (event.action == android.view.KeyEvent.ACTION_DOWN) {
                keyEvent.type = KeyEvent.KEY_DOWN
                if (keyCode in 1..126)
                    pressedKeys[keyCode] = true
            }
            if (event.action == android.view.KeyEvent.ACTION_UP) {
                keyEvent.type = KeyEvent.KEY_UP
                if (keyCode in 1..126)
                    pressedKeys[keyCode] = false
            }
            keyEventsBuffer.add(keyEvent)
        }
        return false
    }

    fun isKeyPressed(keyCode: Int): Boolean = !(keyCode < 0 || keyCode > 127) && pressedKeys[keyCode]

    fun getKeyEvents(): List<KeyEvent> {
        synchronized(this) {
            val len = keyEvents.size
            for (i in 0 until len)
                keyEventPool.free(keyEvents[i])
            keyEvents.clear()
            keyEvents.addAll(keyEventsBuffer)
            keyEventsBuffer.clear()
            return keyEvents
        }
    }
}
