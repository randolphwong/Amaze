package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Randolph on 13/3/2016.
 */
public abstract class PhysicsComponent {
    public abstract void update(float delta);
    public abstract void createBody();
    public abstract Body getBody();
}
