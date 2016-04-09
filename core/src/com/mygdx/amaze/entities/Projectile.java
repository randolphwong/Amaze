package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.ProjectileGraphicsComponent;
import com.mygdx.amaze.components.ProjectileInputComponent;
import com.mygdx.amaze.components.ProjectilePhysicsComponent;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Loo Yi on 3/31/2016.
 */
public class Projectile {

    private PlayScreen screen;

    public static final float THICKNESS = 2;
    public static final float LENGTH = 9;
    public float x;
    public float y;
    public boolean todestroy;
    public boolean exist;

    public float spawnX;
    public float spawnY;

    public Vector2 velocity;

    public ProjectileGraphicsComponent graphicsComponent;
    public ProjectilePhysicsComponent physicsComponent;
    public ProjectileInputComponent inputComponent;

    public Projectile(PlayScreen screen,float x,float y){
        this.screen =screen;
        this.x = spawnX = x;
        this.y = spawnY = y;
        this.exist = false;

        this.velocity = new Vector2(0,0);
        this.inputComponent =  new ProjectileInputComponent(this,screen);
        this.graphicsComponent = new ProjectileGraphicsComponent(this,screen);
        this.physicsComponent = new ProjectilePhysicsComponent(screen.world,this,screen);

    }

    public void update(float delta){

        graphicsComponent.update(delta);
        physicsComponent.update(delta);
        inputComponent.update(delta);

    }

    public void draw(SpriteBatch spriteBatch){
        if(exist){
            graphicsComponent.draw(spriteBatch);
        }
    }

    public void dispose(){graphicsComponent.dispose();}

    public void destroy(){
        todestroy = true;
    }

    public boolean isDestroyed() {
        return exist;
    }

    public Body getBody() {
        return physicsComponent.getBody();
    }
}
