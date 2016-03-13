package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Randolph on 13/3/2016.
 */
public abstract class GraphicsComponent {
    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);
}
