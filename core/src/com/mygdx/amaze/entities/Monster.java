package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import com.mygdx.amaze.components.MonsterGraphicsComponent;
import com.mygdx.amaze.components.MonsterInputComponent;
import com.mygdx.amaze.components.MonsterPhysicsComponent;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 12/3/2016.
 */
public class Monster {

    private static int monsterIdTracker = 0;
    private int id;

    private PlayScreen screen;

    public static final float WIDTH = 48;
    public static final float HEIGHT = 72;

    public Vector2 position;
    public Vector2 spawnLocation;

    public Vector2 velocity;

    // target position that the monster will always move towards
    public Vector2 target;

    public boolean chasingPlayer;
    public Player player;

    // components
    public MonsterInputComponent input;
    public MonsterPhysicsComponent physics;
    public MonsterGraphicsComponent graphics;

    public Monster(PlayScreen screen, Vector2 spawnLocation) {
        id = ++monsterIdTracker;

        this.spawnLocation = spawnLocation;
        position = spawnLocation.cpy();
        target = spawnLocation.cpy();

        this.velocity = new Vector2(0, 0);

        input = new MonsterInputComponent(this);
        physics = new MonsterPhysicsComponent(this, screen.world);
        graphics = new MonsterGraphicsComponent(this);
    }

    public int getId() {
        return id;
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void startChase(Player player) {
        this.player = player;
        chasingPlayer = true;
    }

    public void stopChase() {
        chasingPlayer = false;
    }

    public void update(float delta, NetworkData networkData) {
        input.update(delta, networkData);
        physics.update(delta);
        graphics.update(delta);
    }

    public void draw(SpriteBatch batch) {
        graphics.draw(batch);
    }

    public static void resetIdTracker() {
        monsterIdTracker = 0;
    }

    public void dispose() {
        monsterIdTracker -= 1;
        graphics.dispose();
    }
}
