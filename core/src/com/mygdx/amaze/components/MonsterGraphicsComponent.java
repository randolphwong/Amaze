package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.amaze.entities.Monster;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterGraphicsComponent {

    public static final float RESPAWN_TIME = 2; // time in seconds
    private float respawnTimer;

    private Monster monster;

    private Sprite monsterSprite;

    public MonsterGraphicsComponent(Monster monster) {
        this.monster = monster;

        monsterSprite = new Sprite(new Texture("enemy/enemy_shyo.png"));
        monsterSprite.setCenter(monster.position.x, monster.position.y);
        monsterSprite.setSize(monster.WIDTH, monster.HEIGHT);

    }

    public void update(float delta) {
        if (monster.dead) return;

        if (monster.position.x > 0) {
            monsterSprite.setCenter(monster.position.x, monster.position.y);
        }
        if (monster.destroyed && !monster.dead) {
            monsterSprite.setColor(1, 0, 1, 0.5f);
            respawnTimer += delta;
            if (respawnTimer >= RESPAWN_TIME) {
                monster.dead = true;
                respawnTimer = 0;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        monsterSprite.draw(batch);
    }

    public void dispose() {
        monsterSprite.getTexture().dispose();
    }
}
