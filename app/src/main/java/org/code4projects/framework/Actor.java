/*
 *  Copyright (C) 2016 Salvatore D'Angelo
 *  This file is part of Gdx project.
 *
 *  Droids is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Gdx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework;

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
public class Actor {
    protected int x, y;
    protected int width, height;

    /**
     * Create an actor at position (0, 0) and with 0 size.
     */
    public Actor() {
    }

    /**
     * Create an actor at position (x, y) and with 0 size.
     */
    public Actor(int x, int y) {
        this.x=x;
        this.y=y;
    }

    /**
     * Create an actor at position (x, y) and with size (width, height).
     */
    public Actor(int x, int y, int width, int height) {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    /**
     * Return the x position of the actor.
     */
    public int getX() {
        return x;
    }

    /**
     * Set the x position of the actor.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Return the y position of the actor.
     */
    public int getY() {
        return y;
    }

    /**
     * Set the y position of the actor.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Set the position (x, y) of the actor.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Move the actor from the current position (x, y) to the position (x+deltax, y+deltay).
     */
    public void moveBy(int deltax, int deltay) {
        this.x += deltax;
        this.y += deltay;
    }

    /**
     * Return the width of the actor.
     */
    public int getWidth () {
        return width;
    }

    /**
     * Set the width of the actor.
     */
    public void setWidth (int width) {
        this.width = width;
    }

    /**
     * Return the height of the actor.
     */
    public int getHeight () {
        return height;
    }

    /**
     * Set the height of the actor.
     */
    public void setHeight (int height) {
        this.height = height;
    }

    /**
     * Get the bottom margin of the actor.
     */
    public int getBottom() {
        return y + height;
    }

    /**
     * Get the right margin of the actor.
     */
    public int getRight() {
        return x + width;
    }

    /**
     * Set the size of the actor.
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set the bounds of the actor using primitive numbers.
     */
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Set the bounds of the actor using a {@link Rectangle} object.
     */
    public void setBounds(Rectangle rectangle) {
        this.x = rectangle.getX();
        this.y = rectangle.getY();
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();
    }

    /**
     * Get the bounds of the actor returning a {@link Rectangle} object.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Check if this actor collide with the input actor.
     */
    public boolean collide(Actor actor) {
        return getBounds().overlaps(actor.getBounds());
    }

    /**
     * Check if this actor collide with whatever object or margins of the Game world.
     * By default this method returns always false. Subclasses must implement it if the
     * collision detection feature against all the Game world objects is requested.
     */
    public boolean collide() {
        return false;
    }

    /**
     * Method implement to have collection of actors work properly.
     */
    public boolean equals(Object object) {
        if (object instanceof Actor) {
            Actor actor = (Actor) object;
            if (x==actor.x && y==actor.y && width==actor.width && height==actor.height) {
                return true;
            }
        }
        return false;
    }
}
