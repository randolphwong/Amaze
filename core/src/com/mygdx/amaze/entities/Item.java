package com.mygdx.amaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.ItemGraphicsComponent;
import com.mygdx.amaze.components.ItemPhysicsComponent;
import com.mygdx.amaze.networking.NetworkData;
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
    private Music gunpickup = Gdx.audio.newMusic(Gdx.files.internal("sound/gunpickupsound.mp3"));
    private Music potionpickup = Gdx.audio.newMusic(Gdx.files.internal("sound/potionsound.mp3"));
    private Music shieldpickup = Gdx.audio.newMusic(Gdx.files.internal("sound/shieldsound.mp3"));

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

        this.gunpickup.setVolume(0.5f);
        this.potionpickup.setVolume(0.5f);
        this.shieldpickup.setVolume(0.5f);

        physics = new ItemPhysicsComponent(this, screen.world);

        graphics = new ItemGraphicsComponent(this);
        physics.itemsize = graphics.getItemSprite().getHeight();
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta, NetworkData networkData) {
        if (networkData.isAvailable()) {
            if (networkData.isItemTaken(this)) {
                todestroy = true;
            }
        }
        graphics.update(delta);
        if(todestroy && !destroyed){
            screen.world.destroyBody(getBody());
            switch(this.type){
                case LASER_GUN : gunpickup.play();
                    break;
                case HEALTH_POTION: potionpickup.play();
                    break;
                case SHIELD: shieldpickup.play();
                    break;
            }
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
