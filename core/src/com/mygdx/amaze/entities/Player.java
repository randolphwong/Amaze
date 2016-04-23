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
public class Player extends AbstractPlayer {

    public enum FaceState { UP, DOWN, LEFT, RIGHT }
    public FaceState faceState;

    public boolean collidableWithHole = false;

    public int shotsLeft;
    public int shotsDone;
    public boolean gunequipped;

    // components
    public PlayerInputComponent input;
    public PlayerPhysicsComponent physics;
    public PlayerGraphicsComponent graphics;

    public Player(PlayScreen screen, float x, float y) {
        this.x = spawnX = x;
        this.y = spawnY = y;
        this.screen =screen;
        this.velocity = new Vector2(0, 0);
        this.faceState = FaceState.DOWN;

        input = new PlayerInputComponent(this, screen.hud);
        physics = new PlayerPhysicsComponent(this, screen.world);
        graphics = new PlayerGraphicsComponent(this, physics, screen.hud);
    }

    public void obtainItem(Item item) {
        input.obtainItem(item);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta, Friend friend) {
        input.update(delta);
        physics.update(delta);
        graphics.update(delta, friend);
    }

    public void destroy() {
        todestroy = true;
    }

    public void attacked() {
        input.attacked();
    }

    public void draw(SpriteBatch batch) {
        graphics.draw(batch);
    }

    public void dispose() {
        input.dispose();
        graphics.dispose();
    }

    public void makeCollidableWithHole() {
        physics.makeCollidableWithHole();
    }

    public byte getType() {
        return screen.clientType;
    }
}
