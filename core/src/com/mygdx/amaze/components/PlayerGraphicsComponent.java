package com.mygdx.amaze.components;

import com.badlogic.gdx.Gdx;
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

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerGraphicsComponent extends GraphicsComponent {

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

    private MovementState movementState;

    private float elapsedTime = 0;

    public PlayerGraphicsComponent(Player player, PlayerPhysicsComponent physics, Hud hud) {
        this.player = player;
        this.physics = physics;
        this.hud = hud;

        playerAtlas = new TextureAtlas("player/reyspritesheet.atlas");
        playerSprite = new Sprite(playerAtlas.findRegion("Rey_down_stationary"));
        playerSprite.setCenter(player.x, player.y);
        playerSprite.setSize(player.SIZE, player.SIZE);

        shieldGlowSprite = new Sprite(new Texture("player/glow.png"));
        shieldGlowSprite.setCenter(player.x, player.y);

        setUpAnimation();
    }

    public void setUpAnimation() {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        // up animation
        regions.add(playerAtlas.findRegion("Rey_up_walk", 1));
        regions.add(playerAtlas.findRegion("Rey_up_stationary"));
        regions.add(playerAtlas.findRegion("Rey_up_walk", 2));
        regions.add(playerAtlas.findRegion("Rey_up_stationary"));
        moveUpAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // down animation
        regions.add(playerAtlas.findRegion("Rey_down_walk", 1));
        regions.add(playerAtlas.findRegion("Rey_down_stationary"));
        regions.add(playerAtlas.findRegion("Rey_down_walk", 2));
        regions.add(playerAtlas.findRegion("Rey_down_stationary"));
        moveDownAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // left animation
        regions.add(playerAtlas.findRegion("Rey_left_walk", 1));
        regions.add(playerAtlas.findRegion("Rey_left_stationary"));
        regions.add(playerAtlas.findRegion("Rey_left_walk", 2));
        regions.add(playerAtlas.findRegion("Rey_left_stationary"));
        moveLeftAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // right animation
        regions.add(playerAtlas.findRegion("Rey_right_walk", 1));
        regions.add(playerAtlas.findRegion("Rey_right_stationary"));
        regions.add(playerAtlas.findRegion("Rey_right_walk", 2));
        regions.add(playerAtlas.findRegion("Rey_right_stationary"));
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

        if (player.attacked) {
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

    @Override
    public void update(float delta) {

        updateMovementState();
        updatePlayerSprite();
        updateHealthBar();
    }

    @Override
    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);

        if (player.shielded) {
            shieldGlowSprite.setCenter(player.x, player.y);
            shieldGlowSprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        playerSprite.getTexture().dispose();
        shieldGlowSprite.getTexture().dispose();
        playerAtlas.dispose();
    }
}
