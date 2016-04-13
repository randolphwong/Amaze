package com.mygdx.amaze.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.ItemGraphicsComponent;
import com.mygdx.amaze.components.ItemPhysicsComponent;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Dhanya on 22/03/2016.
 */
public class Item {

    public static final float RESPAWN_TIME = 25; // time in seconds
    public static final float ITEM_SIZE = 32;

    private PlayScreen screen;
    public enum Type{
        HEALTH_POTION, LASER_GUN, SHIELD;
    }
    public Type type;
    private boolean todestroy;
    private boolean destroyed;
    private float respawnTimer;
//    private Body body;
    public float posX;
    public float posY;


    // components
    public ItemPhysicsComponent physics;
    public ItemGraphicsComponent graphics;

    public Item(PlayScreen screen, Type type, float x, float y) {
        this.screen = screen;
        this.type = type;
        this.posX = x;
        this.posY = y;
        this.respawnTimer = 0;

        physics = new ItemPhysicsComponent(this, screen.world);

        graphics = new ItemGraphicsComponent(this);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta, NetworkData networkData) {
        if (networkData.isAvailable()) {
            if (networkData.isItemTaken(this)) {
                todestroy = true;
            }
        }
        graphics.update(delta);
        if(todestroy && !destroyed){
            todestroy = false;
            screen.world.destroyBody(getBody());
            destroyed = true;
            screen.addAvailableItemPosition(new Vector2(posX, posY));
        } else if (destroyed) {
            respawnTimer += delta;
            if (respawnTimer >= RESPAWN_TIME) {
                respawnTimer = 0;
                Vector2 newPosition = screen.getRandomItemPosition();
                posX = newPosition.x;
                posY = newPosition.y;
                physics.createBody();
                destroyed = false;
                System.out.println(type + " has respawned at " + newPosition);
            }
        }
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
