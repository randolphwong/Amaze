package com.mygdx.amaze.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.amaze.screens.PlayScreen;

public abstract class AbstractPlayer {

    public float spawnX;
    public float spawnY;

    public static final float SIZE = 32;
    public float x;
    public float y;

    public boolean todestroy;
    public boolean dead;

    public float health = 99;
    public boolean shielded = false;
    public boolean attacked = false;

    public Vector2 velocity;
    public PlayScreen screen;

    public byte getType() {
        return screen.clientType;
    }
}
