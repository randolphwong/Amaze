package com.mygdx.amaze.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.amaze.collision.CollisionListener;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterPhysicsComponent {

    public static final float DETECTION_RANGE = Player.SIZE * 5;

    private Monster monster;

    private World world;
    private Body body;

    public MonsterPhysicsComponent(Monster monster, World world) {
        this.monster = monster;
        this.world = world;

        createBody();
    }

    public void createBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(monster.spawnLocation);
        body = world.createBody(bodyDef);

        // fixture for main body
        PolygonShape mainShape = new PolygonShape();
        mainShape.setAsBox(monster.WIDTH / 2, monster.HEIGHT / 2);
        FixtureDef mainFixtureDef = new FixtureDef();
        mainFixtureDef.shape = mainShape;
        mainFixtureDef.filter.categoryBits = CollisionListener.MONSTER_BIT;
        mainFixtureDef.filter.maskBits = CollisionListener.WALL_BIT |
                                         CollisionListener.MONSTER_BOUNDARY_BIT | 
                                         CollisionListener.PLAYER_BIT;

        body.createFixture(mainFixtureDef);
        mainShape.dispose();

        // fixture for detecting player (radar)
        CircleShape radarShape = new CircleShape();
        radarShape.setRadius(DETECTION_RANGE);
        FixtureDef radarFixtureDef = new FixtureDef();
        radarFixtureDef.shape = radarShape;
        radarFixtureDef.isSensor = true; // make the fixture non solid
        radarFixtureDef.filter.categoryBits = CollisionListener.MONSTER_RADAR_BIT;
        radarFixtureDef.filter.maskBits = CollisionListener.PLAYER_BIT;

        body.createFixture(radarFixtureDef).setUserData(monster);
        radarShape.dispose();
    }

    /*
     * The chasing mechanism:
     * Monster can only move along the centre of the isles. As such, whenever
     * the player is not in direct line of sight but still within the monster's
     * radar, the monster will have to first move towards the junction before
     * turning towards to the player.
     */
    private void chase() {
        Player player = monster.player;
        // translate the player position on to the imaginary centre line of the isle
        Vector2 translatedPlayerPosition = new Vector2(player.x, player.y);
        if (Math.abs(player.x - monster.spawnLocation.x) <= (1.5f * Player.SIZE)) {
            translatedPlayerPosition.x = monster.spawnLocation.x;
        }
        if (Math.abs(player.y - monster.spawnLocation.y) <= (1.5f * Player.SIZE)) {
            translatedPlayerPosition.y = monster.spawnLocation.y;
        }

        // set the target to be the player when the monster is closer to the player than to the junction
        boolean monsterCloserToPlayer = translatedPlayerPosition.dst2(monster.position) <= translatedPlayerPosition.dst2(monster.spawnLocation);
        boolean monsterVeryCloseToJunction = monster.position.dst2(monster.spawnLocation) < 3;
        if (monsterCloserToPlayer || monsterVeryCloseToJunction) {
            monster.target = translatedPlayerPosition;
        } else {
            monster.target = monster.spawnLocation;
        }
    }

    private void moveTowardsTarget() {
        Vector2 newVelocity = new Vector2(0, 0);

        // move the monster when it has not yet reached the target position (within an error margin of 2 pixels)
        if (monster.target.x - monster.position.x < -2) {
            newVelocity.x = -110;
        } else if (monster.target.x - monster.position.x > 2) {
            newVelocity.x = 110;
        } else if (monster.target.y - monster.position.y < -2) {
            newVelocity.y = -110;
        } else if (monster.target.y - monster.position.y > 2) {
            newVelocity.y = 110;
        }
        monster.velocity.set(newVelocity);
    }

    public void update(float delta) {
        if (monster.chasingPlayer) {
            chase();
        }
        moveTowardsTarget();

        body.setLinearVelocity(monster.velocity);
        monster.position.set(body.getPosition());
    }

    public Body getBody() {
        return body;
    }
}
