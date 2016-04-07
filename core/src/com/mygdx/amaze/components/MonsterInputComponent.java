package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.networking.GameData;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterInputComponent {

    private Monster monster;

    public MonsterInputComponent(Monster monster) {
        this.monster = monster;
    }

    public void update(float delta, GameData gameData) {
        // set the target here only if the monster is chasing the remote player
        if (gameData != null && (gameData.monsterChasing & (1 << (monster.getId() - 1))) != 0) {
            monster.target.x = gameData.monsterPosition[monster.getId() - 1].x;
            monster.target.y = gameData.monsterPosition[monster.getId() - 1].y;
        }
    }
}
