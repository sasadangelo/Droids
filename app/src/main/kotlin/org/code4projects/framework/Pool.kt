/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * A pool of objects that can be reused to avoid allocation.
 *
 * @author mzechner
 */
class Pool<T>(private val factory: PoolObjectFactory<T>, private val maxSize: Int) {
    interface PoolObjectFactory<T> {
        fun createObject(): T
    }

    private val freeObjects: MutableList<T> = ArrayList(maxSize)

    fun newObject(): T {
        return if (freeObjects.size == 0) {
            factory.createObject()
        } else {
            freeObjects.removeAt(freeObjects.size - 1)
        }
    }

    fun free(obj: T) {
        if (freeObjects.size < maxSize) {
            freeObjects.add(obj)
        }
    }
}
