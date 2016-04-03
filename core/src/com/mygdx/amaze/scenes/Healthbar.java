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

    private Sprite healthbarBar;
    private static float healthBarWidth = Hud.gutterWidth * 0.75f;
    private static float healthBarHeight = healthBarWidth * 0.25f;

    /**
     *
     * @param x: x coordinate for the centre of the sprite
     * @param y: y coordinate for the centre of the sprite
     */
    public Healthbar(float x, float y) {
        //healthbarBackground = new Sprite(new Texture("health/healthbar-background.png"));
        healthbarBar = new Sprite(new Texture("health/healthbar-bar.png"));

        setDrawable(new SpriteDrawable(healthbarBar));

        setSize(healthBarWidth, healthBarHeight);
        setPosition(x - (healthBarWidth / 2), y - (healthBarHeight / 2));
    }

    public void setHealth(float health) {
        setScaleX(health / 100f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}

