package com.mygdx.amaze.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.scenes.Healthbar;
import com.mygdx.amaze.scenes.Hud;

/**
 * Created by Randolph on 13/3/2016.
 */
public class FriendGraphicsComponent extends GraphicsComponent {

    private enum MovementState { MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, STATIONARY };

    private Friend friend;
    private FriendPhysicsComponent physics;

    private Sprite friendSprite;
    private TextureAtlas friendAtlas;

    private Animation moveLeftAnimation;
    private Animation moveRightAnimation;
    private Animation moveUpAnimation;
    private Animation moveDownAnimation;

    private MovementState movementState;
    private float previousX;
    private float previousY;

    private float elapsedTime = 0;

    public FriendGraphicsComponent(Friend friend, FriendPhysicsComponent physics) {
        this.friend = friend;
        this.physics = physics;

        friendAtlas = new TextureAtlas("friend/lukespritesheet.atlas");
        friendSprite = new Sprite(friendAtlas.findRegion("Luke_down_stationary"));
        friendSprite.setCenter(friend.x, friend.y);
        friendSprite.setSize(friend.SIZE, friend.SIZE);

        previousX = friend.x;
        previousY = friend.y;

        setUpAnimation();
    }

    public void setUpAnimation() {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        // up animation
        regions.add(friendAtlas.findRegion("Luke_up_walk", 1));
        regions.add(friendAtlas.findRegion("Luke_up_stationary"));
        regions.add(friendAtlas.findRegion("Luke_up_walk", 2));
        regions.add(friendAtlas.findRegion("Luke_up_stationary"));
        moveUpAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // down animation
        regions.add(friendAtlas.findRegion("Luke_down_walk", 1));
        regions.add(friendAtlas.findRegion("Luke_down_stationary"));
        regions.add(friendAtlas.findRegion("Luke_down_walk", 2));
        regions.add(friendAtlas.findRegion("Luke_down_stationary"));
        moveDownAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // left animation
        regions.add(friendAtlas.findRegion("Luke_left_walk", 1));
        regions.add(friendAtlas.findRegion("Luke_left_stationary"));
        moveLeftAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // right animation
        regions.add(friendAtlas.findRegion("Luke_right_walk", 1));
        regions.add(friendAtlas.findRegion("Luke_right_stationary"));
        moveRightAnimation = new Animation(1 / 5f, regions);
        regions.clear();
    }

    private void updateMovementState() {
        if (friend.x < previousX) {
            movementState = MovementState.MOVE_LEFT;
        } else if (friend.x > previousX) {
            movementState = MovementState.MOVE_RIGHT;
        } else if (friend.y < previousY) {
            movementState = MovementState.MOVE_DOWN;
        } else if (friend.y > previousY) {
            movementState = MovementState.MOVE_UP;
        } else {
            movementState = MovementState.STATIONARY;
        }
        previousX = friend.x;
        previousY = friend.y;
    }

    public void updateFriendSprite() {
        switch (movementState) {
            case MOVE_UP:
                friendSprite.setRegion(moveUpAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_DOWN:
                friendSprite.setRegion(moveDownAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_LEFT:
                friendSprite.setRegion(moveLeftAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_RIGHT:
                friendSprite.setRegion(moveRightAnimation.getKeyFrame(elapsedTime, true));
                break;
        }

        // update elapsedTime for animation
        elapsedTime += Gdx.graphics.getDeltaTime();

        friendSprite.setCenter(friend.x, friend.y);
    }

    @Override
    public void update(float delta) {

        updateMovementState();
        updateFriendSprite();
    }

    @Override
    public void draw(SpriteBatch batch) {
        friendSprite.draw(batch);
    }
}
