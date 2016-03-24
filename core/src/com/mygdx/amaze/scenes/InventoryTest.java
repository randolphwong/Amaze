package com.mygdx.amaze.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by Randolph on 21/3/2016.
 */
public class InventoryTest extends Table {

    private static final int ITEM_SIZE = 20;

    /**
     * @param x: x coordinate for the centre of the inventory
     * @param y: y coordinate for the centre of the inventory
     */
    public InventoryTest(float x, float y) {
        setPosition(x, y);
        add(new Image(new Texture("enemy/enemy_rock.png"))).size(ITEM_SIZE, ITEM_SIZE);
        add(new Image(new Texture("enemy/enemy_rock.png"))).size(ITEM_SIZE, ITEM_SIZE);
        row();
        add(new Image(new Texture("enemy/enemy_rock.png"))).size(ITEM_SIZE, ITEM_SIZE);
        add(new Image(new Texture("enemy/enemy_rock.png"))).size(ITEM_SIZE, ITEM_SIZE);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
