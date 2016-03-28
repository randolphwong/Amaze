package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.networking.GameData;

/**
 * Created by Randolph on 13/3/2016.
 */
public class FriendPhysicsComponent extends PhysicsComponent {

    private Friend friend;

    private World world;
    private Body body;

    public FriendPhysicsComponent(Friend friend, World world) {
        this.friend = friend;
        this.world = world;

        //createBody();
    }

    public void createBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(friend.SIZE / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;

        bodyDef.position.set(friend.spawnX, friend.spawnY);
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        circle.dispose();
    }

    public void update(float delta) {
        // check if collided with monster
/*
 *        Boolean dead = (Boolean) body.getUserData();
 *        int friendHealth = friend.health;
 *        if (dead != null && dead == true && friendHealth <=0) {
 *            world.destroyBody(body);
 *            createBody();
 *            friend.health = 99;
 *            return;
 *        }
 *
 *        body.setLinearVelocity(friend.velocity);
 *        friend.x = body.getPosition().x;
 *        friend.y = body.getPosition().y;
 */


    }

    public Body getBody() {
        return body;
    }
}
