package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.ItemGraphicsComponent;
import com.mygdx.amaze.components.ItemPhysicsComponent;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Dhanya on 22/03/2016.
 */
public class Item {

    private PlayScreen screen;
    public enum Type{
        HEALTH_POTION, LASER_GUN, SHIELD;
    }
    public Type type;
    private boolean todestroy;
    private boolean destroyed;
//    private Body body;
    public float posX;
    public float posY;


    // components
    public ItemPhysicsComponent physics;
    public ItemGraphicsComponent graphics;

    public Item(PlayScreen screen, Type type, float x, float y) {
        this.screen = screen;
        this.type = type;
        this.posX = x;
        this.posY = y;

        physics = new ItemPhysicsComponent(this, screen.world);

        graphics = new ItemGraphicsComponent(this);
        physics.itemsize = graphics.getItemSprite().getHeight();
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta, GameData gameData) {
        if (gameData != null) {
            switch (type) {
            case HEALTH_POTION: todestroy |= gameData.potionDestroyed; break;
            case LASER_GUN: todestroy |= gameData.laserDestroyed; break;
            case SHIELD: todestroy |= gameData.shieldDestroyed; break;
            }
        }
//        physics.update(delta);
        graphics.update(delta);
        if(todestroy && !destroyed){
            screen.world.destroyBody(getBody());
            destroyed = true;
        }
    }

    public void destroy(){
        todestroy = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void draw(SpriteBatch batch) {
        if(!destroyed) {
            graphics.draw(batch);
        }
    }

    public void dispose() {
        graphics.dispose();
    }
}
