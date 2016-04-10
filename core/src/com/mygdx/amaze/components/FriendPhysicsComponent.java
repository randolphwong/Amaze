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
        bodyDef.position.set(friend.spawnX, friend.spawnY);
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(friend.SIZE / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.filter.maskBits = 0; // collides with nothing

        body.createFixture(fixtureDef);

        circle.dispose();
    }

    public void update(float delta) {
        float toMoveX = 0;
        float toMoveY = 0;

        float deltaX = friend.targetX - friend.x;
        float deltaY = friend.targetY - friend.y;

        /*
         * As long as the absolute difference between current position and the target position (from
         * networkData) is not greater than 30, we will smoothly move the Friend's sprite to the
         * target position. Otherwise, it is assumed that Friend has died/somehow teleported to
         * somewhere far. In that case, we will just set friend position = target position.
         */
        if (Math.abs(deltaX) <= 30 && Math.abs(deltaY) <= 30) {
            // 1.67 pixels per frame is about 100 pixels per sec
            if (deltaX < -2) {
                toMoveX = -1.67f;
            } else if (deltaX > 2) {
                toMoveX = 1.67f;
            } else if (deltaY < -2) {
                toMoveY = -1.67f;
            } else if (deltaY > 2) {
                toMoveY = 1.67f;
            }
            friend.x += toMoveX;
            friend.y += toMoveY;
        } else {
            friend.x = friend.targetX;
            friend.y = friend.targetY;
        }

    }

    public Body getBody() {
        return body;
    }
}
