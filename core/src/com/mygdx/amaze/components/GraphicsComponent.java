package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Randolph on 13/3/2016.
 */
public abstract class GraphicsComponent implements Disposable {
    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);
}
