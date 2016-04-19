package com.mygdx.amaze.networking;

import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Player;

public class ItemRespawnRequest extends Request {

    private Item item;

    public ItemRespawnRequest(Item item) {
        this.item = item;
    }

    public void makeRequest(NetworkData networkData) {
        requestId = networkData.requestItemRespawn(item);
    }

    public void execute() {
        item.respawned();
    }
}
