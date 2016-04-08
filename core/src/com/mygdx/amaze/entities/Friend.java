package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.FriendGraphicsComponent;
import com.mygdx.amaze.components.FriendInputComponent;
import com.mygdx.amaze.components.FriendPhysicsComponent;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 12/3/2016.
 */
public class Friend {

    public float spawnX;
    public float spawnY;

    public static final float SIZE = 32;
    public float x;
    public float y;

    public int health = 99;
    public boolean attacked = false;
    public boolean shielded = false;

    //inventory
    public Item[] Inventory;

    public Vector2 velocity;

    // components
    public FriendInputComponent input;
    public FriendPhysicsComponent physics;
    public FriendGraphicsComponent graphics;

    public Friend(PlayScreen screen, float x, float y) {
        this.x = spawnX = x;
        this.y = spawnY = y;

        this.velocity = new Vector2(0, 0);

        input = new FriendInputComponent(this);
        physics = new FriendPhysicsComponent(this, screen.world);
        graphics = new FriendGraphicsComponent(this, physics);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta, NetworkData networkData) {
        input.update(delta, networkData);
        //physics.update(delta);
        graphics.update(delta);
    }

    public void draw(SpriteBatch batch) {
        graphics.draw(batch);
    }

    public void dispose() {
        graphics.dispose();
    }
}
