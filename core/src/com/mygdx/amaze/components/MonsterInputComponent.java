package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.utilities.Coord;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterInputComponent {

    private Monster monster;

    public MonsterInputComponent(Monster monster) {
        this.monster = monster;
    }

    public void update(float delta, NetworkData networkData) {
        if (networkData.isAvailable()) {
            Coord remoteMonsterPosition = networkData.monsterPosition(monster);
            if (remoteMonsterPosition != null) {
                // x < 0 => remote monster is dead
                if (remoteMonsterPosition.x < 0) {
                    monster.todestroy = true;
                }

                // set the target here only if the monster is chasing the remote player
                if (networkData.isMonsterChasing(monster) && !monster.isChasing()) {
                    monster.target.x = remoteMonsterPosition.x;
                    monster.target.y = remoteMonsterPosition.y;
                }
            }
        }
    }
}
