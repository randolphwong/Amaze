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

    private TextureRegion normal;
    private TextureRegion white;
    private Animation blinkAnimation;
    private Sprite shieldGlowSprite;

    private MovementState movementState;
    private float previousX;
    private float previousY;

    private float elapsedTime = 0;

    public FriendGraphicsComponent(Friend friend, FriendPhysicsComponent physics) {
        this.friend = friend;
        this.physics = physics;

        friendAtlas = new TextureAtlas("friend/friend.atlas");
        friendSprite = new Sprite(friendAtlas.findRegion("friend_down", 1));
        friendSprite.setCenter(friend.x, friend.y);
        friendSprite.setSize(friend.SIZE, friend.SIZE);

        shieldGlowSprite = new Sprite(new Texture("player/glow.png"));
        shieldGlowSprite.setCenter(friend.x, friend.y);

        setUpAnimation();

        previousX = friend.x;
        previousY = friend.y;

        setUpAnimation();
    }

    public void setUpAnimation() {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        // up animation
        regions.add(friendAtlas.findRegion("friend_up", 0));
        regions.add(friendAtlas.findRegion("friend_up", 1));
        regions.add(friendAtlas.findRegion("friend_up", 2));
        regions.add(friendAtlas.findRegion("friend_up", 1));
        moveUpAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // down animation
        regions.add(friendAtlas.findRegion("friend_down", 0));
        regions.add(friendAtlas.findRegion("friend_down", 1));
        regions.add(friendAtlas.findRegion("friend_down", 2));
        regions.add(friendAtlas.findRegion("friend_down", 1));
        moveDownAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // left animation
        regions.add(friendAtlas.findRegion("friend_left", 0));
        regions.add(friendAtlas.findRegion("friend_left", 1));
        regions.add(friendAtlas.findRegion("friend_left", 2));
        regions.add(friendAtlas.findRegion("friend_left", 1));
        moveLeftAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // right animation
        regions.add(friendAtlas.findRegion("friend_right", 0));
        regions.add(friendAtlas.findRegion("friend_right", 1));
        regions.add(friendAtlas.findRegion("friend_right", 2));
        regions.add(friendAtlas.findRegion("friend_right", 1));
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

        if (friend.attacked) {
            if (blinkAnimation.getKeyFrame(elapsedTime, true) == white) {
                friendSprite.setColor(0, 0, 0, 0.5f);
            } else {
                friendSprite.setColor(1, 1, 1, 1);
            }
        } else {
            friendSprite.setColor(1, 1, 1, 1);
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

        if (friend.shielded) {
            shieldGlowSprite.setCenter(friend.x, friend.y);
            shieldGlowSprite.draw(batch);
        }
    }

    @Override
    public void dispose() {
        friendSprite.getTexture().dispose();
        shieldGlowSprite.getTexture().dispose();
        friendAtlas.dispose();
    }
}
