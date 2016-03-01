package com.mygdx.amaze.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.amaze.Amaze;

public class Player extends Sprite {

    private Amaze game;

    // box2d
    private World world;
    private Body body;

    // texture
    private Texture playerImg;

    public Player(Amaze game) {
        super(new Texture("Rey_down_stationary.png"));
        this.game = game;
        this.setBounds(60, 0, 16, 16); // some magic numbers
        this.world = game.getWorld();

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

    public void update() {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    public void move(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public void dispose() {

    }
}
