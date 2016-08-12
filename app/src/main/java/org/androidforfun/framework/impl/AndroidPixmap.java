package org.androidforfun.framework.impl;

import android.graphics.Bitmap;

import org.androidforfun.framework.Graphics.PixmapFormat;
import org.androidforfun.framework.Pixmap;

public class AndroidPixmap implements Pixmap {
    Bitmap bitmap;
    PixmapFormat format;
    
    public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public PixmapFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }

    public int getColor(int x, int y) {
        return bitmap.getPixel(x, y);
    }

    public void setColor(int x, int y, int color) {
        bitmap.setPixel(x, y, color);
    }
}
