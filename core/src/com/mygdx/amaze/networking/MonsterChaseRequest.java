package com.mygdx.amaze.networking;

import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;

public class MonsterChaseRequest extends Request {

    private Player player;
    private Monster monster;

    public MonsterChaseRequest(Player player, Monster monster) {
        this.player = player;
        this.monster = monster;
    }

    public void makeRequest(NetworkData networkData) {
        requestId = networkData.requestMonsterChase(monster);
    }

    public void execute() {
        monster.startChase(player);
    }
}
