package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.Monster;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterInputComponent {

    private Monster monster;
    private boolean moveLeft;

    public MonsterInputComponent(Monster monster) {
        this.monster = monster;
        moveLeft = true;
    }

    public void update(float delta) {
        if (monster.x <= 200) {
            moveLeft = false;
        } else if (monster.x >= 850) {
            moveLeft = true;
        }

        if (moveLeft) {
            monster.velocity.x = -80;
        } else {
            monster.velocity.x = 80;
        }
    }
}
