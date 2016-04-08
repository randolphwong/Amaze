package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.networking.NetworkData;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterInputComponent {

    private Monster monster;

    public MonsterInputComponent(Monster monster) {
        this.monster = monster;
    }

    public void update(float delta, NetworkData networkData) {
        // set the target here only if the monster is chasing the remote player
        if (networkData.isAvailable()) {
            if (networkData.isMonsterChasing(monster)) {
                monster.target.x = networkData.monsterPosition(monster).x;
                monster.target.y = networkData.monsterPosition(monster).y;
            }
        }
    }
}
