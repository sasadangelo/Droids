package org.androidforfun.framework;

import org.androidforfun.framework.Graphics.PixmapFormat;

public interface Pixmap {
    int getWidth();
    int getHeight();
    PixmapFormat getFormat();
    void dispose();
    int getColor(int x, int y);
    void setColor(int x, int y, int color);
}
