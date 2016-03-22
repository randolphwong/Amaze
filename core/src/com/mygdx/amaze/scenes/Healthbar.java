package com.mygdx.amaze.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by Randolph on 21/3/2016.
 */
public class Healthbar extends Image {

    private Sprite healthbarSprite;
    private static final float healthBarWidth = 50;
    private static final float healthBarHeight = 50;

    /**
     *
     * @param x: x coordinate for the centre of the sprite
     * @param y: y coordinate for the centre of the sprite
     */
    public Healthbar(float x, float y) {
        healthbarSprite = new Sprite(new Texture("enemy/enemy_rock.png"));
        setDrawable(new SpriteDrawable(healthbarSprite));

        setSize(healthBarWidth, healthBarHeight);
        setPosition(x - (healthBarWidth / 2), y - (healthBarHeight / 2));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
