package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.amaze.entities.Monster;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterGraphicsComponent extends GraphicsComponent {

    private Monster monster;

    private Sprite monsterSprite;

    public MonsterGraphicsComponent(Monster monster) {
        this.monster = monster;

        monsterSprite = new Sprite(new Texture("enemy/enemy_shyo.png"));
        monsterSprite.setCenter(monster.x, monster.y);
        monsterSprite.setSize(monster.WIDTH, monster.HEIGHT);

    }

    @Override
    public void update(float delta) {
        monsterSprite.setCenter(monster.x, monster.y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        monsterSprite.draw(batch);
    }
}
