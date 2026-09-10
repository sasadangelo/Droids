/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * A a 2D rectangle defined by its corner point in the top left and its extents in x
 * (width) and y (height).
 * This class is inspired to the LibGdx Actor class whose authors are Mario Zechner and Nathan
 * Sweet.
 *
 * @author Salvatore D'Angelo
 */
class Rectangle(var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = 0) {

    /**
     * Clone a rectangle.
     */
    constructor(rect: Rectangle) : this(rect.x, rect.y, rect.width, rect.height)

    /**
     * Set the position (x, y) and the size (width, height) of the rectangle.
     */
    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    /**
     * Set the position (x, y) of the rectangle.
     */
    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    /**
     * Set the size (width, height) of the rectangle.
     */
    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    /**
     * Check if the rectangle contains the point (x, y).
     */
    fun contains(x: Int, y: Int): Boolean =
        x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height

    /**
     * Check if the rectangle contains entirely the input rectangle.
     */
    fun contains(rectangle: Rectangle): Boolean {
        val xmin = rectangle.x
        val xmax = xmin + rectangle.width

        val ymin = rectangle.y
        val ymax = ymin + rectangle.height

        return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width)) &&
            ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height))
    }

    /**
     * Check if the rectangle intasersects the input rectangle. This method is very useful for collision
     * detection and the methods collide of the [Actor] class are based on it.
     */
    fun overlaps(r: Rectangle): Boolean =
        contains(r.x, r.y) || contains(r.x + r.width - 1, r.y) || contains(r.x, r.y + r.height - 1) ||
            contains(r.x + r.width - 1, r.y + r.height - 1)

    /**
     * Set the position (x, y) and the size (width, height) of this rectangle using the input rectangle.
     */
    fun set(rect: Rectangle) {
        x = rect.x
        y = rect.y
        width = rect.width
        height = rect.height
    }
}
