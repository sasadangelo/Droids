package org.androidforfun.framework;

public interface Screen {
    void update(float deltaTime);
    void draw(float deltaTime);
    void pause();
    void resume();
    void dispose();
}
