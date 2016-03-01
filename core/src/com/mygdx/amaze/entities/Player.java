package com.mygdx.amaze.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import com.mygdx.amaze.Amaze;

public class Player extends Sprite {

    private Amaze game;

    // player states
    private enum PlayerState { MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN, STATIONARY };
    private PlayerState state;

    // box2d
    private World world;
    private Body body;

    // texture
    private Texture playerImg;
    private TextureAtlas playerAtlas;

    // animation
    private Animation moveLeftAnimation;
    private Animation moveRightAnimation;
    private Animation moveUpAnimation;
    private Animation moveDownAnimation;
    private float elapsedTime = 0;


    public Player(Amaze game) {
        this.game = game;
        this.setBounds(60, 0, 16, 16); // some magic numbers
        this.world = game.getWorld();

        playerAtlas = new TextureAtlas("player/reyspritesheet.atlas");
        setRegion(playerAtlas.findRegion("Rey_down_stationary"));

        setUpAnimation();

        state = PlayerState.STATIONARY;

        makeBody();
    }

    public void makeBody() {
        // BodyDef
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        body = world.createBody(bodyDef);

        // Fixture
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(getWidth() / 2);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        shape.dispose();
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

    public void updateState() {
        if (body.getLinearVelocity().x < 0) {
            state = PlayerState.MOVE_LEFT;
        } else if (body.getLinearVelocity().x > 0) {
            state = PlayerState.MOVE_RIGHT;
        } else if (body.getLinearVelocity().y < 0) {
            state = PlayerState.MOVE_DOWN;
        } else if (body.getLinearVelocity().y > 0) {
            state = PlayerState.MOVE_UP;
        } else {
            state = PlayerState.STATIONARY;
        }
    }

    public void update() {

        updateState();

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        switch (state) {
            case STATIONARY:
                setRegion(playerAtlas.findRegion("Rey_down_stationary"));
                break;
            case MOVE_UP:
                setRegion(moveUpAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_DOWN:
                setRegion(moveDownAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_LEFT:
                setRegion(moveLeftAnimation.getKeyFrame(elapsedTime, true));
                break;
            case MOVE_RIGHT:
                setRegion(moveRightAnimation.getKeyFrame(elapsedTime, true));
                break;
        }


        // update elapsedTime for animation
        elapsedTime += Gdx.graphics.getDeltaTime();
    }

    public void move(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public void dispose() {
        playerAtlas.dispose();
    }
}
