package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.PlayerGraphicsComponent;
import com.mygdx.amaze.components.PlayerInputComponent;
import com.mygdx.amaze.components.PlayerPhysicsComponent;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 12/3/2016.
 */
public class Player {

    public float spawnX;
    public float spawnY;

    public static final float SIZE = 32;
    public float x;
    public float y;

    public int health = 99;
    public int shotsLeft;
    public boolean shielded = false;
    public int faceState;
    public boolean gunequipped;

    //inventory
    public Item[] Inventory;

    public Vector2 velocity;
    public PlayScreen screen;
    // components
    public PlayerInputComponent input;
    public PlayerPhysicsComponent physics;
    public PlayerGraphicsComponent graphics;

    public Player(PlayScreen screen, float x, float y) {
        this.x = spawnX = x;
        this.y = spawnY = y;
        this.screen =screen;

        this.velocity = new Vector2(0, 0);
        this.faceState = 2;

        input = new PlayerInputComponent(this, screen.hud);
        physics = new PlayerPhysicsComponent(this, screen.world);
        graphics = new PlayerGraphicsComponent(this, physics, screen.hud);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta) {
        input.update(delta);
        physics.update(delta);
        graphics.update(delta);
        faceState();
        gunState();
        System.out.println(faceState);

    }

    public void draw(SpriteBatch batch) {
        graphics.draw(batch);
    }

    public void dispose() {
        graphics.dispose();
    }

    public void gunState(){
        if(shotsLeft<=0){
            gunequipped =false;
        }
    }

    public void faceState (){
        if(this.velocity.x>0 && this.velocity.y==0){
            this.faceState = 1;
        }else if(this.velocity.x<0 && this.velocity.y==0){
            this.faceState = 3;
        }else if(this.velocity.x==0 && this.velocity.y>0){
            this.faceState = 0;
        }else if(this.velocity.x ==0 && this.velocity.y<0) {
            this.faceState = 2;
        }
    }

    public void fireLaser(){
        if(gunequipped || shotsLeft >0){
            this.screen.projectile =new Projectile(this.screen,this.x,this.y);
//            screen.projectileFired =true;
            shotsLeft--;
        }
    }
}
