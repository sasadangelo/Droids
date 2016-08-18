/*
 *  Copyright (C) 2016 Salvatore D'Angelo
 *  This file is part of Gdx project.
 *
 *  Gdx is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Droids is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework;

/**
 * A a 2D rectangle defined by its corner point in the top left and its extents in x
 * (width) and y (height).
 * This class is inspired to the LibGdx Actor class whose authors are Mario Zechner and Nathan
 * Sweet.
 *
 * @author Salvatore D'Angelo
 */
public class Rectangle {
    protected int x, y;
    protected int width, height;

    /**
     * Create a rectangle at position (0, 0) and with 0 size.
     */
    public Rectangle() {
    }

    /**
     * Create a rectangle at position (x, y) and with size (width, height).
     */
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Clone a rectangle.
     */
    public Rectangle(Rectangle rect) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
    }

    /**
     * Set the position (x, y) and the size (width, height) of the rectangle.
     */
    public void set(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Set the position (x, y) of the rectangle.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the size (width, height) of the rectangle.
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Check if the rectangle contains the point (x, y).
     */
    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    /**
     * Check if the rectangle contains entirely the input rectangle.
     */
    public boolean contains(Rectangle rectangle) {
        int xmin = rectangle.x;
        int xmax = xmin + rectangle.width;

        int ymin = rectangle.y;
        int ymax = ymin + rectangle.height;

        return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
            && ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
    }

    /**
     * Check if the rectangle intasersects the input rectangle. This method is very useful for collision
     * detection and the methods collide of the {@link Actor} class are based on it.
     */
    public boolean overlaps(Rectangle r) {
        return (contains(r.x, r.y) || contains(r.x+r.width-1, r.y) || contains(r.x, r.y+r.width-1) ||
                contains(r.x+r.width-1, r.y+r.width-1));
    }

    /**
     * Set the position (x, y) and the size (width, height) of this rectangle using the input rectangle.
     */
    public void set(Rectangle rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    /**
     * Get the x position of the rectangle.
     */
    public int getX () {
        return x;
    }

    /**
     * Set the x position of the rectangle.
     */
    public void setX (int x) {
        this.x = x;
    }

    /**
     * Get the y position of the rectangle.
     */
    public int getY () {
        return y;
    }

    /**
     * Set the y position of the rectangle.
     */
    public void setY (int y) {
        this.y = y;
    }

    /**
     * Get the width of the rectangle.
     */
    public int getWidth () {
        return width;
    }

    /**
     * Set the width of the rectangle.
     */
    public void setWidth (int width) {
        this.width = width;
    }

    /**
     * Get the height of the rectangle.
     */
    public int getHeight () {
        return height;
    }

    /**
     * Set the height of the rectangle.
     */
    public void setHeight (int height) {
        this.height = height;
    }
}
