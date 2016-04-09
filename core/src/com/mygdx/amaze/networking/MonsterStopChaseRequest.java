package com.mygdx.amaze.networking;

import com.mygdx.amaze.entities.Monster;

public class MonsterStopChaseRequest extends Request {

    private Monster monster;

    public MonsterStopChaseRequest(Monster monster) {
        this.monster = monster;
    }

    public void makeRequest(NetworkData networkData) {
        requestId = networkData.requestMonsterStopChase(monster);
    }

    public void execute() {
        monster.stopChase();
    }
}
