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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import com.mygdx.amaze.entities.AbstractPlayer;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.scenes.Healthbar;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.screens.PlayScreen;
import com.mygdx.amaze.utilities.Const;


/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerGraphicsComponent extends AbstractPlayerGraphicsComponent {

    private Hud hud;

    private PlayerPhysicsComponent physics;

    private Sprite doorCompassSprite;
    private Sprite friendCompassSprite;
    private Vector2 doorPosition;

    private float elapsedTime = 0;

    public PlayerGraphicsComponent(AbstractPlayer player, PlayerPhysicsComponent physics, Hud hud) {
        super(player);
        this.physics = physics;
        this.hud = hud;
        doorPosition = new Vector2();

        setPlayerType(player.getType() == Const.MASTER_CLIENT ? new String("player") : new String("friend"));
        System.out.println("player type: " + playerType);
        setupSprite();
        setUpAnimation();
    }

    @Override
    protected void setupSprite() {
        super.setupSprite();

        friendCompassSprite = new Sprite(new Texture("player/red_compass.png"));
        friendCompassSprite.setCenter(player.x, player.y);
        friendCompassSprite.setSize(player.SIZE + 30, player.SIZE + 30);
        friendCompassSprite.setOriginCenter();

        doorCompassSprite = new Sprite(new Texture("player/green_compass.png"));
        doorCompassSprite.setCenter(player.x, player.y);
        doorCompassSprite.setSize(player.SIZE + 30, player.SIZE + 30);
        doorCompassSprite.setOriginCenter();
    }

    @Override
    protected void updateMovementState() {
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
        super.update(delta);
        updateHealthBar();
        updateCompass(friend);
    }

    public void draw(SpriteBatch batch) {
        super.draw(batch);
        doorCompassSprite.draw(batch);
        friendCompassSprite.draw(batch);
    }

    public void dispose() {
        super.dispose();
        doorCompassSprite.getTexture().dispose();
        friendCompassSprite.getTexture().dispose();
    }
}
