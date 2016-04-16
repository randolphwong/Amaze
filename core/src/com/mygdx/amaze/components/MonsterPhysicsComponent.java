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
    public static final float DETECTION_RANGE_SQUARED = DETECTION_RANGE * DETECTION_RANGE;

    private Monster monster;

    private World world;
    private Body body;

    private Vector2 playerPosition;
    private Vector2 previousPlayerPosition;
    private int isPlayerMoveVertically;
    private int isPlayerMoveHorizontally;
    private static final int playerMovementBufferSize = 20;

    public MonsterPhysicsComponent(Monster monster, World world) {
        this.monster = monster;
        this.world = world;
        this.playerPosition = new Vector2();
        this.previousPlayerPosition = new Vector2();
        this.isPlayerMoveVertically = 0;
        this.isPlayerMoveHorizontally = 0;

        createBody();
    }

    public void createBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(monster.spawnLocation);
        body = world.createBody(bodyDef);

        // fixture for main body
        CircleShape mainShape = new CircleShape();
        mainShape.setRadius(monster.WIDTH / 2);
        FixtureDef mainFixtureDef = new FixtureDef();
        mainFixtureDef.shape = mainShape;
        mainFixtureDef.filter.categoryBits = CollisionListener.MONSTER_BIT;
        mainFixtureDef.filter.maskBits = CollisionListener.WALL_BIT |
                                         CollisionListener.MONSTER_BOUNDARY_BIT | 
                                         CollisionListener.PLAYER_BIT |
                                         CollisionListener.PROJECTILE_BIT;

        body.createFixture(mainFixtureDef).setUserData(monster);
        mainShape.dispose();

        // fixture for detecting player (radar)
        CircleShape radarShape = new CircleShape();
        radarShape.setRadius(DETECTION_RANGE);
        FixtureDef radarFixtureDef = new FixtureDef();
        radarFixtureDef.shape = radarShape;
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
        Vector2 translatedPlayerPosition = getTranslatedPlayerPosition();

        // set the target to be the player when the monster is closer to the player than to the junction
        boolean monsterCloserToPlayer = translatedPlayerPosition.dst2(monster.position) <= translatedPlayerPosition.dst2(monster.spawnLocation);
        boolean monsterVeryCloseToJunction = monster.position.dst2(monster.spawnLocation) < 3;
        if (monsterCloserToPlayer || monsterVeryCloseToJunction) {
            monster.target.set(translatedPlayerPosition);
        } else {
            monster.target.set(monster.spawnLocation);
        }
        /*
         * If target is too far (eg. player died and respawned), set target = current position. This
         * is required to prevent mosnter from continuously chasing while client is awaiting
         * server's confirmation on stopping chase
         */
        if (monster.position.dst2(playerPosition) > DETECTION_RANGE_SQUARED) {
            monster.target.set(monster.position);
        }
    }

    /*
     * translate/map current player position on to the imaginary centre line of the isle
     * 
     * post-condition: return value has either of the following properties:
     * 1. translatedPlayerPosition.x = monster.spawnLocation.x
     * 2. translatedPlayerPosition.y = monster.spawnLocation.y
     */
    private Vector2 getTranslatedPlayerPosition() {
        Vector2 translatedPlayerPosition = new Vector2(playerPosition.x, playerPosition.y);
        float deltaX = Math.abs(playerPosition.x - monster.spawnLocation.x);
        float deltaY = Math.abs(playerPosition.y - monster.spawnLocation.y);

        if ((deltaX > (1.8f * Player.SIZE)) && (deltaY > (1.8f * Player.SIZE))) { // player not within monster boundary
            translatedPlayerPosition.set(monster.position);
        } else {
            if (deltaX <= (1.8f * Player.SIZE)) {
                translatedPlayerPosition.x = monster.spawnLocation.x;
            }
            if (deltaY <= (1.8f * Player.SIZE)) {
                translatedPlayerPosition.y = monster.spawnLocation.y;
            }
        }

        if (playerMovingHorizontally()) {
            if (deltaY <= (1.8f * Player.SIZE)) {
                translatedPlayerPosition.x = playerPosition.x;
            }
        } else if (playerMovingVertically()) {
            if (deltaX <= (1.8f * Player.SIZE)) {
                translatedPlayerPosition.y = playerPosition.y;
            }
        }
        return translatedPlayerPosition;
    }

    /*
     * determines whether player appears to be moving horizontally by using buffer to keep track of
     * previous movements
     */
    private boolean playerMovingHorizontally() {
        if (previousPlayerPosition.x == 0 && previousPlayerPosition.y == 0) {
            previousPlayerPosition.set(playerPosition);
            return false;
        }
        if (playerPosition.dst2(previousPlayerPosition) > 1) {
            if (playerPosition.x != previousPlayerPosition.x) {
                if (isPlayerMoveHorizontally < playerMovementBufferSize) {
                    isPlayerMoveHorizontally++;
                }
            } else {
                if (isPlayerMoveHorizontally > 0) {
                    isPlayerMoveHorizontally--;
                }
            }
            previousPlayerPosition.set(playerPosition);
        }

        return isPlayerMoveHorizontally == playerMovementBufferSize;
    }

    private boolean playerMovingVertically() {
        if (previousPlayerPosition.x == 0 && previousPlayerPosition.y == 0) {
            previousPlayerPosition.set(playerPosition);
            return false;
        }
        if (playerPosition.dst2(previousPlayerPosition) > 1) {
            if (playerPosition.x != previousPlayerPosition.x) {
                if (isPlayerMoveVertically < playerMovementBufferSize) {
                    isPlayerMoveVertically++;
                }
            } else {
                if (isPlayerMoveVertically > 0) {
                    isPlayerMoveVertically--;
                }
            }
            previousPlayerPosition.set(playerPosition);
        }

        return isPlayerMoveVertically == playerMovementBufferSize;
    }

    private void moveTowardsTarget() {
        Vector2 newVelocity = new Vector2(0, 0);

        // move the monster when it has not yet reached the target position (within an error margin of 2 pixels)
        if (monster.target.x - monster.position.x < -2) {
            newVelocity.x = -90;
        } else if (monster.target.x - monster.position.x > 2) {
            newVelocity.x = 90;
        } else if (monster.target.y - monster.position.y < -2) {
            newVelocity.y = -90;
        } else if (monster.target.y - monster.position.y > 2) {
            newVelocity.y = 90;
        }
        monster.velocity.set(newVelocity);
    }

    public void update(float delta) {
        if (monster.isChasing()) {
            playerPosition.set(monster.player.x, monster.player.y);
            chase();
        } else {
            isPlayerMoveVertically = 0;
            isPlayerMoveHorizontally = 0;
        }
        moveTowardsTarget();
        body.setLinearVelocity(monster.velocity);
        monster.position.set(body.getPosition());
    }

    public Body getBody() {
        return body;
    }
}
