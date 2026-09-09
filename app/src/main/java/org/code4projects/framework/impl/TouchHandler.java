/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl;

import java.util.List;

import android.view.View.OnTouchListener;

import org.code4projects.framework.Input.TouchEvent;

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
