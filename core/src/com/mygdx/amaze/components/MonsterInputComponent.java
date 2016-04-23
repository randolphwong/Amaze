package com.mygdx.amaze.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.utilities.Coord;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterInputComponent {

    private Monster monster;

    private Sound sound_laugh = Gdx.audio.newSound(Gdx.files.internal("sound/kefkalaugh.mp3"));
    private Sound sound_death = Gdx.audio.newSound(Gdx.files.internal("sound/monsterdeath.mp3"));

    public MonsterInputComponent(Monster monster) {
        this.monster = monster;
    }

    public void startChase() {
        sound_laugh.play();
    }

    public void update(float delta, NetworkData networkData) {
        if (monster.destroyed) return;

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

        if (monster.todestroy) {
            sound_death.play();
            monster.screen.world.destroyBody(monster.getBody());
            monster.position.set(-1f, -1f);
            monster.destroyed = true;
            monster.todestroy = false;
        }
    }

    public void dispose() {
        sound_laugh.dispose();
        sound_death.dispose();
    }
}
