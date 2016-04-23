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
public class Friend extends AbstractPlayer {

    public float targetX;
    public float targetY;

    // components
    public FriendInputComponent input;
    public FriendPhysicsComponent physics;
    public FriendGraphicsComponent graphics;

    public Friend(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.targetX = this.x = spawnX = x;
        this.targetY = this.y = spawnY = y;

        this.velocity = new Vector2(0, 0);

        input = new FriendInputComponent(this, screen);
        physics = new FriendPhysicsComponent(this, screen.world);
        graphics = new FriendGraphicsComponent(this);
    }

    public void update(float delta, NetworkData networkData) {
        input.update(delta, networkData);
        physics.update(delta);
        graphics.update(delta);
    }

    public void draw(SpriteBatch batch) {
        graphics.draw(batch);
    }

    public void dispose() {
        graphics.dispose();
    }
}
