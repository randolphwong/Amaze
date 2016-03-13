package com.mygdx.amaze.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.amaze.entities.Player;

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerGraphicsComponent extends GraphicsComponent {

    private enum MovementState { MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, STATIONARY };

    private Player player;
    private PlayerPhysicsComponent physics;

    private Sprite playerSprite;
    private TextureAtlas playerAtlas;

    private Animation moveLeftAnimation;
    private Animation moveRightAnimation;
    private Animation moveUpAnimation;
    private Animation moveDownAnimation;

    private MovementState movementState;

    private float elapsedTime = 0;

    public PlayerGraphicsComponent(Player player, PlayerPhysicsComponent physics) {
        this.player = player;
        this.physics = physics;

        playerAtlas = new TextureAtlas("player/reyspritesheet.atlas");
        playerSprite = new Sprite(playerAtlas.findRegion("Rey_down_stationary"));
        playerSprite.setCenter(player.x, player.y);
        playerSprite.setSize(player.SIZE, player.SIZE);

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

    @Override
    public void update(float delta) {

        updateMovementState();

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

        // update elapsedTime for animation
        elapsedTime += Gdx.graphics.getDeltaTime();

        playerSprite.setCenter(player.x, player.y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);
    }
}
