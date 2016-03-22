package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.PlayerGraphicsComponent;
import com.mygdx.amaze.components.PlayerInputComponent;
import com.mygdx.amaze.components.PlayerPhysicsComponent;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 12/3/2016.
 */
public class Player {

    public float spawnX;
    public float spawnY;

    public static final float SIZE = 32;
    public float x;
    public float y;

    public Vector2 velocity;

    // components
    public PlayerInputComponent input;
    public PlayerPhysicsComponent physics;
    public PlayerGraphicsComponent graphics;

    public Player(PlayScreen screen, float x, float y) {
        this.x = spawnX = x;
        this.y = spawnY = y;

        this.velocity = new Vector2(0, 0);

        input = new PlayerInputComponent(this, screen.hud.getTouchpad());
        physics = new PlayerPhysicsComponent(this, screen.world);
        graphics = new PlayerGraphicsComponent(this, physics, screen.hud);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta) {
        input.update(delta);
        physics.update(delta);
        graphics.update(delta);
    }

    public void draw(SpriteBatch batch) {
        graphics.draw(batch);
    }
}
