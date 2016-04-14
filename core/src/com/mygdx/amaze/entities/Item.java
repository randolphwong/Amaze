package com.mygdx.amaze.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.ItemGraphicsComponent;
import com.mygdx.amaze.components.ItemPhysicsComponent;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.screens.PlayScreen;
import com.mygdx.amaze.utilities.ItemType;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.networking.ItemRespawnRequest;
import com.mygdx.amaze.networking.RequestManager;

/**
 * Created by Dhanya on 22/03/2016.
 */
public class Item {

    public static final float RESPAWN_TIME = 25; // time in seconds
    public static final float ITEM_SIZE = 32;

    private PlayScreen screen;
    public ItemType type;
    private boolean todestroy;
    private boolean destroyed;
    private float respawnTimer;
    private boolean isRespawning;

    public float posX;
    public float posY;


    // components
    public ItemPhysicsComponent physics;
    public ItemGraphicsComponent graphics;

    public Item(PlayScreen screen, ItemType type, float x, float y) {
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

    // TODO: move networking code into ItemInputComponent
    // TODO: move others into ItemPhysicsComponent
    public void update(float delta, NetworkData networkData) {
        if (networkData.isAvailable()) {
            if (networkData.isItemTaken(this)) {
                /*
                 * checking isRespawning is required in order to prevent scenario where master
                 * client make a item respawn request, and at the same time it receives a message
                 * from server indicating that the item has been taken (which is actually old info).
                 */
                if (!destroyed && !isRespawning) {
                    todestroy = true;
                }
            } else if (destroyed) {
                if (screen.clientType == Const.SLAVE_CLIENT && networkData.itemPosition(this) != null) {
                    if (respawnTimer >= RESPAWN_TIME) {
                        respawnTimer = 0;
                        posX = networkData.itemPosition(this).x;
                        posY = networkData.itemPosition(this).y;
                        physics.createBody();
                        destroyed = false;
                    }
                }
            }
        }
        graphics.update(delta);
        if(todestroy && !destroyed){
            todestroy = false;
            screen.world.destroyBody(getBody());
            destroyed = true;
            if (screen.clientType == Const.MASTER_CLIENT) {
                screen.addAvailableItemPosition(new Vector2(posX, posY));
            }
        } else if (destroyed) {
            respawnTimer += delta;
            if (screen.clientType == Const.MASTER_CLIENT) {
                if (respawnTimer >= RESPAWN_TIME) {
                    respawnTimer = 0;
                    Vector2 newPosition = screen.getRandomItemPosition();
                    posX = newPosition.x;
                    posY = newPosition.y;
                    physics.createBody();
                    destroyed = false;
                    isRespawning = true;
                    RequestManager.getInstance().newRequest(new ItemRespawnRequest(this));
                }
            }
        }
    }

    public void respawned() {
        isRespawning = false;
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
