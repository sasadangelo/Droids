package org.androidforfun.framework;

public class TextStyle {
    public enum Align {
        LEFT,
        CENTER,
        RIGHT,
        JUSTIFY
    }

    public enum Style {
        NORMAL,
        BOLD,
        ITALIC
    }

    private int color;
    private int textSize;
    private Align align = Align.LEFT;
    private Style style = Style.NORMAL;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }
}
