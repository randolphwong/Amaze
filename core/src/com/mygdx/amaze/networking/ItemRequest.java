package com.mygdx.amaze.networking;

import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Player;

public class ItemRequest extends Request {

    private Player player;
    private Item item;

    public ItemRequest(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    public void makeRequest(NetworkData networkData) {
        requestId = networkData.requestItem(item);
    }

    public void execute() {
        player.obtainItem(item);
    }
}
