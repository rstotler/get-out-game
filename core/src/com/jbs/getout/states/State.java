package com.jbs.getout.states;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class State {

    // Variables //
    public static OrthographicCamera camera;

    // Functions //
    protected abstract void handleInput();
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch spriteBatch);
    public abstract void dispose();
}
