package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.ItemInputComponent;
import com.mygdx.amaze.components.ItemGraphicsComponent;
import com.mygdx.amaze.components.ItemPhysicsComponent;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.screens.PlayScreen;
import com.mygdx.amaze.utilities.ItemType;

/**
 * Created by Dhanya on 22/03/2016.
 */
public class Item {

    public static final float ITEM_SIZE = 32;

    public PlayScreen screen;
    public ItemType type;


    public boolean todestroy;
    public boolean destroyed;

    public float posX;
    public float posY;


    // components
    public ItemInputComponent input;
    public ItemPhysicsComponent physics;
    public ItemGraphicsComponent graphics;

    public Item(PlayScreen screen, ItemType type, float x, float y) {
        this.screen = screen;
        this.type = type;
        this.posX = x;
        this.posY = y;

        input = new ItemInputComponent(this);
        physics = new ItemPhysicsComponent(this, screen.world);
        graphics = new ItemGraphicsComponent(this);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta, NetworkData networkData) {
        input.update(delta, networkData);
        graphics.update(delta);
    }

    public void respawned() {
        input.respawned();
    }

    public void destroy(){
        todestroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void draw(SpriteBatch batch) {
        if(!destroyed) {
            graphics.draw(batch);
        }
    }

    public void dispose() {
        graphics.dispose();
    }
}
