package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.utilities.Coord;
import com.mygdx.amaze.networking.ItemRespawnRequest;
import com.mygdx.amaze.networking.RequestManager;
import com.mygdx.amaze.utilities.Const;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Randolph on 13/3/2016.
 */
public class ItemInputComponent {

    public static final float RESPAWN_TIME = 25; // time in seconds

    private Item item;

    private float respawnTimer;
    private boolean isRespawning;

    public ItemInputComponent(Item item) {
        this.item = item;
    }

    public void respawned() {
        isRespawning = false;
    }

    public void update(float delta, NetworkData networkData) {
        byte clientType = item.screen.clientType;
        if (networkData.isAvailable()) {
            if (networkData.isItemTaken(item)) {
                /*
                 * checking isRespawning is required in order to prevent scenario where master
                 * client make a item respawn request, and at the same time it receives a message
                 * from server indicating that the item has been taken (which is actually old info).
                 */
                if (!item.destroyed && !isRespawning) {
                    item.todestroy = true;
                }
            } else if (item.destroyed) {
                if (clientType == Const.SLAVE_CLIENT && networkData.itemPosition(item) != null) {
                    if (respawnTimer >= RESPAWN_TIME) {
                        respawnTimer = 0;
                        item.posX = networkData.itemPosition(item).x;
                        item.posY = networkData.itemPosition(item).y;
                        item.physics.createBody();
                        item.destroyed = false;
                    }
                }
            }
        }
        if(item.todestroy && !item.destroyed){
            item.todestroy = false;
            item.screen.world.destroyBody(item.getBody());
            item.destroyed = true;
            if (clientType == Const.MASTER_CLIENT) {
                item.screen.addAvailableItemPosition(new Vector2(item.posX, item.posY));
            }
        } else if (item.destroyed) {
            respawnTimer += delta;
            if (clientType == Const.MASTER_CLIENT) {
                if (respawnTimer >= RESPAWN_TIME) {
                    respawnTimer = 0;
                    Vector2 newPosition = item.screen.getRandomItemPosition();
                    item.posX = newPosition.x;
                    item.posY = newPosition.y;
                    item.physics.createBody();
                    item.destroyed = false;
                    isRespawning = true;
                    RequestManager.getInstance().newRequest(new ItemRespawnRequest(item));
                }
            }
        }
    }
}
