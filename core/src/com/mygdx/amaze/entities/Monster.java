package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.amaze.components.MonsterGraphicsComponent;
import com.mygdx.amaze.components.MonsterInputComponent;
import com.mygdx.amaze.components.MonsterPhysicsComponent;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 12/3/2016.
 */
public class Monster {

    private PlayScreen screen;

    public static final float WIDTH = 48;
    public static final float HEIGHT = 72;
    public float x;
    public float y;

    public float spawnX;
    public float spawnY;
    public boolean todestroy;
    public boolean destroyed;

    public Vector2 velocity;

    // components
    public MonsterInputComponent input;
    public MonsterPhysicsComponent physics;
    public MonsterGraphicsComponent graphics;

    public Monster(PlayScreen screen, float x, float y) {
        this.x = spawnX = x;
        this.y = spawnY = y;
        this.destroyed =false;
        this.screen =screen;
        this.velocity = new Vector2(0, 0);

        input = new MonsterInputComponent(this);
        physics = new MonsterPhysicsComponent(this, screen.world);
        graphics = new MonsterGraphicsComponent(this);
    }

    public Body getBody() {
        return physics.getBody();
    }

    public void update(float delta) {
        if(!destroyed) {
            input.update(delta);
            physics.update(delta);
            graphics.update(delta);
        }
        if(todestroy && !destroyed){
            System.out.println(getBody());
            screen.world.destroyBody(getBody());
            destroyed = true;
        }
    }

    public void destroy(){
        todestroy =true;
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
