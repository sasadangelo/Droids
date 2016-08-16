/*
 *  Copyright (C) 2016 Mario Zechner
 *  This file is part of Framework for book Beginning Android Games.
 *
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License.
 */
package org.androidforfun.framework.impl;

import java.util.List;

import android.view.View.OnTouchListener;

import org.androidforfun.framework.Input.TouchEvent;

/*
 * This is the handler used to manage touch events. This class is specialized by SingleTouchHandler
 * or MultiTouchHandler depending on Android version.
 *
 * @author mzechner
 */
public interface TouchHandler extends OnTouchListener {
    boolean isTouchDown(int pointer);
    int getTouchX(int pointer);
    int getTouchY(int pointer);
    List<TouchEvent> getTouchEvents();
}
