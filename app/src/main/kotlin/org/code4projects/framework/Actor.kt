/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework

/**
 * An actor is a rectangular object having a x, y position in the game space, a width and an height.
 * An actor include also built in collision detection mechanism.
 * Whenever in your game you can represents an object as a rectangular shape and it is necessary
 * perform collision detection then it is advisable your object inherits from this class.
 * This class is inspired to the LibGdx Actor class whose authors are Mario Zechner and Nathan
 * Sweet.
 *
 * @author Salvatore D'Angelo
 */
open class Actor(var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = 0) {

    /**
     * Set the position (x, y) of the actor.
     */
    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    /**
     * Move the actor from the current position (x, y) to the position (x+deltax, y+deltay).
     */
    fun moveBy(deltax: Int, deltay: Int) {
        x += deltax
        y += deltay
    }

    /**
     * Get the bottom margin of the actor.
     */
    fun getBottom(): Int = y + height

    /**
     * Get the right margin of the actor.
     */
    fun getRight(): Int = x + width

    /**
     * Set the size of the actor.
     */
    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    /**
     * Set the bounds of the actor using primitive numbers.
     */
    fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    /**
     * Set the bounds of the actor using a [Rectangle] object.
     */
    fun setBounds(rectangle: Rectangle) {
        x = rectangle.x
        y = rectangle.y
        width = rectangle.width
        height = rectangle.height
    }

    /**
     * Get the bounds of the actor returning a [Rectangle] object.
     */
    fun getBounds(): Rectangle = Rectangle(x, y, width, height)

    /**
     * Check if this actor collide with the input actor.
     */
    fun collide(actor: Actor): Boolean = getBounds().overlaps(actor.getBounds())

    /**
     * Check if this actor collide with whatever object or margins of the Game world.
     * By default this method returns always false. Subclasses must implement it if the
     * collision detection feature against all the Game world objects is requested.
     */
    open fun collide(): Boolean = false

    /**
     * Method implement to have collection of actors work properly.
     */
    override fun equals(other: Any?): Boolean {
        if (other is Actor) {
            return x == other.x && y == other.y && width == other.width && height == other.height
        }
        return false
    }
}
