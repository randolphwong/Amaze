package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.amaze.collision.CollisionListener;
import com.mygdx.amaze.entities.Player;

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerPhysicsComponent extends PhysicsComponent {

    public static final float RESPAWN_TIME = 2; // time in seconds

    private Player player;

    private World world;
    private Body body;
    private float respawnTimer;

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

        if (player.collidableWithHole) {
            fixtureDef.filter.maskBits |= CollisionListener.HOLE_BIT;
        }

        body.createFixture(fixtureDef).setUserData(player);

        circle.dispose();
    }

    public void makeCollidableWithHole() {
        // make player collidable with ground holes
        player.collidableWithHole = true;
        if (!player.dead) {
            Filter originalMask = player.getBody().getFixtureList().get(0).getFilterData();
            originalMask.maskBits |= CollisionListener.HOLE_BIT;
            player.getBody().getFixtureList().get(0).setFilterData(originalMask);
        }
    }

    public void update(float delta) {
        // check if collided with monster
        if ((player.health <= 0 || player.todestroy) && !player.dead) {
            player.todestroy = false;
            world.destroyBody(body);
            player.dead = true;
            return;
        }

        if (player.dead) {
            respawnTimer += delta;
            if (respawnTimer >= RESPAWN_TIME) {
                player.dead = false;
                respawnTimer = 0;
                createBody();
                player.health = 99;
                player.x = player.spawnX;
                player.y = player.spawnY;
            }
        } else {
            body.setLinearVelocity(player.velocity);
            player.x = body.getPosition().x;
            player.y = body.getPosition().y;
        }
    }

    public Body getBody() {
        return body;
    }
}
