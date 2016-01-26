package org.androidforfun.retrogames.framework;

import org.androidforfun.retrogames.framework.Graphics.PixmapFormat;

public interface Pixmap {
    public int getWidth();
    public int getHeight();
    public PixmapFormat getFormat();
    public void dispose();
    public int getColor(int x, int y);
    public void setColor(int x, int y, int color);
}
