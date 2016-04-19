package com.mygdx.amaze.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.scenes.Healthbar;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.screens.PlayScreen;
import com.badlogic.gdx.math.MathUtils;


/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerGraphicsComponent {

    private enum MovementState { MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, STATIONARY };

    private Hud hud;

    private Player player;
    private PlayerPhysicsComponent physics;

    private Sprite playerSprite;
    private TextureAtlas playerAtlas;

    private Animation moveLeftAnimation;
    private Animation moveRightAnimation;
    private Animation moveUpAnimation;
    private Animation moveDownAnimation;

    private TextureRegion normal;
    private TextureRegion white;
    private Animation blinkAnimation;
    private Sprite shieldGlowSprite;
    private Sprite doorCompassSprite;
    private Sprite friendCompassSprite;
    private Vector2 doorPosition;

    private MovementState movementState;
    private Rectangle level1DoorRect;
    private Vector2 level1DoorPos;

    private float elapsedTime = 0;

    public PlayerGraphicsComponent(Player player, PlayerPhysicsComponent physics, Hud hud) {
        this.player = player;
        this.physics = physics;
        this.hud = hud;

        playerAtlas = new TextureAtlas("player/player.atlas");
        playerSprite = new Sprite(playerAtlas.findRegion("player_down", 1));
        playerSprite.setCenter(player.x, player.y);
        playerSprite.setSize(player.SIZE, player.SIZE);

        shieldGlowSprite = new Sprite(new Texture("player/glow.png"));
        shieldGlowSprite.setCenter(player.x, player.y);

        friendCompassSprite = new Sprite(new Texture("player/red_compass.png"));
        friendCompassSprite.setCenter(player.x, player.y);
        friendCompassSprite.setSize(player.SIZE + 30, player.SIZE + 30);
        friendCompassSprite.setOriginCenter();

        doorCompassSprite = new Sprite(new Texture("player/green_compass.png"));
        doorCompassSprite.setCenter(player.x, player.y);
        doorCompassSprite.setSize(player.SIZE + 30, player.SIZE + 30);
        doorCompassSprite.setOriginCenter();
        doorPosition = new Vector2();

        setUpAnimation();
    }

    public void setUpAnimation() {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        // up animation
        regions.add(playerAtlas.findRegion("player_up", 0));
        regions.add(playerAtlas.findRegion("player_up", 1));
        regions.add(playerAtlas.findRegion("player_up", 2));
        regions.add(playerAtlas.findRegion("player_up", 1));
        moveUpAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // down animation
        regions.add(playerAtlas.findRegion("player_down", 0));
        regions.add(playerAtlas.findRegion("player_down", 1));
        regions.add(playerAtlas.findRegion("player_down", 2));
        regions.add(playerAtlas.findRegion("player_down", 1));
        moveDownAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // left animation
        regions.add(playerAtlas.findRegion("player_left", 0));
        regions.add(playerAtlas.findRegion("player_left", 1));
        regions.add(playerAtlas.findRegion("player_left", 2));
        regions.add(playerAtlas.findRegion("player_left", 1));
        moveLeftAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // right animation
        regions.add(playerAtlas.findRegion("player_right", 0));
        regions.add(playerAtlas.findRegion("player_right", 1));
        regions.add(playerAtlas.findRegion("player_right", 2));
        regions.add(playerAtlas.findRegion("player_right", 1));
        moveRightAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // attacked animation (blink)
        normal = new TextureRegion();
        white = new TextureRegion();
        regions.add(normal);
        regions.add(white);
        blinkAnimation = new Animation(1 / 5f, regions);
        regions.clear();
    }

    private void updateMovementState() {
        if (player.dead) {
            movementState = MovementState.STATIONARY;
            return;
        }
        Vector2 playerVelocity = physics.getBody().getLinearVelocity();
        if (playerVelocity.x < 0) {
            movementState = MovementState.MOVE_LEFT;
        } else if (playerVelocity.x > 0) {
            movementState = MovementState.MOVE_RIGHT;
        } else if (playerVelocity.y < 0) {
            movementState = MovementState.MOVE_DOWN;
        } else if (playerVelocity.y > 0) {
            movementState = MovementState.MOVE_UP;
        } else {
            movementState = MovementState.STATIONARY;
        }
    }

    public void updatePlayerSprite() {
        switch (movementState) {
            case MOVE_UP:
                playerSprite.setRegion(moveUpAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_DOWN:
                playerSprite.setRegion(moveDownAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_LEFT:
                playerSprite.setRegion(moveLeftAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_RIGHT:
                playerSprite.setRegion(moveRightAnimation.getKeyFrame(elapsedTime, true));
                break;
        }

        if (player.dead) {
            playerSprite.setColor(1, 0, 1, 0.8f);
        } else if (player.attacked && !player.shielded) {
            if (blinkAnimation.getKeyFrame(elapsedTime, true) == white) {
                playerSprite.setColor(0, 0, 0, 0.5f);
            } else {
                playerSprite.setColor(1, 1, 1, 1);
            }
        } else {
            playerSprite.setColor(1, 1, 1, 1);
        }

        // update elapsedTime for animation
        elapsedTime += Gdx.graphics.getDeltaTime();

        playerSprite.setCenter(player.x, player.y);
    }

    public void updateHealthBar() {
        hud.getHealthbar().setHealth(player.health);
    }

    public void updateCompass(Friend friend) {
        int level = player.screen.level;
        doorCompassSprite.setCenter(player.x, player.y);
        doorCompassSprite.setOriginCenter();
        doorPosition = PlayScreen.doorRect[level-1].getCenter(doorPosition);
        float toRotate = -MathUtils.atan2(doorPosition.x - player.x, doorPosition.y - player.y);
        doorCompassSprite.rotate(MathUtils.radiansToDegrees * toRotate - doorCompassSprite.getRotation());

        friendCompassSprite.setCenter(player.x, player.y);
        friendCompassSprite.setOriginCenter();
        toRotate = -MathUtils.atan2(friend.x - player.x, friend.y - player.y);
        friendCompassSprite.rotate(MathUtils.radiansToDegrees * toRotate - friendCompassSprite.getRotation());
    }

    public void update(float delta, Friend friend) {

        updateMovementState();
        updatePlayerSprite();
        updateHealthBar();
        updateCompass(friend);
        shieldGlowSprite.setCenter(player.x, player.y);
    }

    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);
        doorCompassSprite.draw(batch);
        friendCompassSprite.draw(batch);
        if (player.shielded) {
            shieldGlowSprite.draw(batch);
        }
    }

    public void dispose() {
        playerSprite.getTexture().dispose();
        shieldGlowSprite.getTexture().dispose();
        doorCompassSprite.getTexture().dispose();
        friendCompassSprite.getTexture().dispose();
        playerAtlas.dispose();
    }
}
