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
import com.mygdx.amaze.entities.AbstractPlayer;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.scenes.Healthbar;
import com.mygdx.amaze.scenes.Hud;

public abstract class AbstractPlayerGraphicsComponent {

    protected enum MovementState { MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, STATIONARY };

    protected String playerType;
    protected AbstractPlayer player;

    protected Sprite playerSprite;
    protected TextureAtlas playerAtlas;

    protected Animation moveLeftAnimation;
    protected Animation moveRightAnimation;
    protected Animation moveUpAnimation;
    protected Animation moveDownAnimation;

    protected TextureRegion normal;
    protected TextureRegion white;
    protected Animation blinkAnimation;
    protected Sprite shieldGlowSprite;

    protected MovementState movementState;

    protected float elapsedTime = 0;

    public AbstractPlayerGraphicsComponent(AbstractPlayer player) {
        this.player = player;
    }

    protected void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    protected void setupSprite() {
        playerAtlas = new TextureAtlas(playerType + "/" + playerType + ".atlas");
        playerSprite = new Sprite(playerAtlas.findRegion(playerType + "_down", 1));
        playerSprite.setCenter(player.x, player.y);
        playerSprite.setSize(player.SIZE, player.SIZE);

        shieldGlowSprite = new Sprite(new Texture("player/glow.png"));
        shieldGlowSprite.setCenter(player.x, player.y);
    }

    protected void setUpAnimation() {
        Array<TextureRegion> regions = new Array<TextureRegion>();

        // up animation
        regions.add(playerAtlas.findRegion(playerType + "_up", 0));
        regions.add(playerAtlas.findRegion(playerType + "_up", 1));
        regions.add(playerAtlas.findRegion(playerType + "_up", 2));
        regions.add(playerAtlas.findRegion(playerType + "_up", 1));
        moveUpAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // down animation
        regions.add(playerAtlas.findRegion(playerType + "_down", 0));
        regions.add(playerAtlas.findRegion(playerType + "_down", 1));
        regions.add(playerAtlas.findRegion(playerType + "_down", 2));
        regions.add(playerAtlas.findRegion(playerType + "_down", 1));
        moveDownAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // left animation
        regions.add(playerAtlas.findRegion(playerType + "_left", 0));
        regions.add(playerAtlas.findRegion(playerType + "_left", 1));
        regions.add(playerAtlas.findRegion(playerType + "_left", 2));
        regions.add(playerAtlas.findRegion(playerType + "_left", 1));
        moveLeftAnimation = new Animation(1 / 5f, regions);
        regions.clear();

        // right animation
        regions.add(playerAtlas.findRegion(playerType + "_right", 0));
        regions.add(playerAtlas.findRegion(playerType + "_right", 1));
        regions.add(playerAtlas.findRegion(playerType + "_right", 2));
        regions.add(playerAtlas.findRegion(playerType + "_right", 1));
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

    protected abstract void updateMovementState();

    protected void updatePlayerSprite() {
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
        } else if (player.attacked) {
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

    public void update(float delta) {
        updateMovementState();
        updatePlayerSprite();
        shieldGlowSprite.setCenter(player.x, player.y);
    }

    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);

        if (player.shielded) {
            shieldGlowSprite.draw(batch);
        }
    }

    public void dispose() {
        playerSprite.getTexture().dispose();
        shieldGlowSprite.getTexture().dispose();
        playerAtlas.dispose();
    }
}
