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
 * <p>
 * Represents the style of a text. A text has a size, a color, an alignment and a style (normal,
 * bold, italic).
 * </p>
 *
 * @author Salvatore D'Angelo
 */
public class TextStyle {
    /**
     * <p>
     * Text could be aligned on the left, right, centered or justified.
     * </p>
     */
    public enum Align {
        LEFT,
        CENTER,
        RIGHT,
        JUSTIFY
    }

    /**
     * <p>
     * Text could be normal, bold, italic.
     * </p>
     */
    public enum Style {
        NORMAL,
        BOLD,
        ITALIC
    }

    private int color;
    private int textSize;
    private Align align = Align.LEFT;
    private Style style = Style.NORMAL;

    /**
     * Gets the color of the text.
     *
     * @return the color of the text.
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color of the text.
     *
     * @param color the color of the text.
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Gets the alignment of the text. Possible values are LEFT, CENTER, RIGHT and JUSTIFY.
     *
     * @return the alignment of the text.
     */
    public Align getAlign() {
        return align;
    }

    /**
     * Sets the alignment of the text.
     *
     * @param align the alignment of the text.
     */
    public void setAlign(Align align) {
        this.align = align;
    }

    /**
     * Gets the size of the text.
     *
     * @return the size of the text.
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Sets the size of the text.
     *
     * @param textSize the size of the text.
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * Gets the style of the text. Possible values are NORMAL, BOLD and ITALIC.
     *
     * @return the style of the text.
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Sets the style of the text.
     *
     * @param style the style of the text.
     */
    public void setStyle(Style style) {
        this.style = style;
    }
}
