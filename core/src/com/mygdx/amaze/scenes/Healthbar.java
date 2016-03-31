package com.mygdx.amaze.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Created by Randolph on 21/3/2016.
 */
public class Healthbar extends Image {

    private Sprite healthbarSprite;
    private Sprite healthbarSprite2;
    private Sprite healthbarSprite3;
    private static float healthBarWidth = Hud.gutterWidth * 0.75f;
    private static float healthBarHeight = healthBarWidth * 0.6f;

    /**
     *
     * @param x: x coordinate for the centre of the sprite
     * @param y: y coordinate for the centre of the sprite
     */
    public Healthbar(float x, float y) {
        healthbarSprite = new Sprite(new Texture("health/1_lives.png"));
        healthbarSprite2 = new Sprite(new Texture("health/2_lives.png"));
        healthbarSprite3 = new Sprite(new Texture("health/3_lives.png"));

        setDrawable(new SpriteDrawable(healthbarSprite3));

        setSize(healthBarWidth, healthBarHeight);
        setPosition(x - (healthBarWidth / 2), y - (healthBarHeight / 2));
    }

    public void threeLives() {
        setDrawable(new SpriteDrawable(healthbarSprite3));
    }

    public void twoLives() {
        setDrawable(new SpriteDrawable(healthbarSprite2));
    }

    public void oneLife() {
        setDrawable(new SpriteDrawable(healthbarSprite));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}

