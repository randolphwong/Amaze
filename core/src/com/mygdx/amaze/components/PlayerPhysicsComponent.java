package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.amaze.collision.CollisionListener;
import com.mygdx.amaze.entities.Player;

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerPhysicsComponent extends PhysicsComponent {

    private Player player;

    private World world;
    private Body body;

    public PlayerPhysicsComponent(Player player, World world) {
        this.player = player;
        this.world = world;

        createBody();
    }

    public void createBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(player.spawnX, player.spawnY);
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(player.SIZE / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;

        fixtureDef.filter.categoryBits = CollisionListener.PLAYER_BIT;
        fixtureDef.filter.maskBits = CollisionListener.WALL_BIT |
                                     CollisionListener.ITEM_BIT |
                                     CollisionListener.MONSTER_BIT |
                                     CollisionListener.MONSTER_RADAR_BIT;

        body.createFixture(fixtureDef).setUserData(player);

        circle.dispose();
    }

    public void update(float delta) {
        // check if collided with monster
        int playerHealth = player.health;
        if (playerHealth <= 0) {
            world.destroyBody(body);
            createBody();
            player.health = 99;
            return;
        }

        body.setLinearVelocity(player.velocity);
        player.x = body.getPosition().x;
        player.y = body.getPosition().y;


    }

    public Body getBody() {
        return body;
    }
}
