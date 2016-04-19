package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import com.mygdx.amaze.collision.CollisionListener;
import com.mygdx.amaze.entities.Item;


/**
 * Created by Dhanya on 24/03/2016.
 */
public class ItemPhysicsComponent extends PhysicsComponent{
    private Item item;
    private Body body;
    private World world;
    private boolean taken;

    public ItemPhysicsComponent(Item item, World world){
        this.world = world;
        this.item = item;
        createBody();
    }

    @Override
    public void update(float delta) {
//        item.posX = body.getPosition().x; // <<this part
//        item.posY = body.getPosition().y;
    }

    public void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(item.posX, item.posY);
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(item.ITEM_SIZE / 2);
        FixtureDef itemFixtureDef = new FixtureDef();
        itemFixtureDef.shape = shape;

        itemFixtureDef.isSensor = true;
        itemFixtureDef.filter.categoryBits = CollisionListener.ITEM_BIT;
        itemFixtureDef.filter.maskBits = CollisionListener.PLAYER_BIT;

        body.createFixture(itemFixtureDef).setUserData(item);
        shape.dispose();
    }

    @Override
    public Body getBody() {
        return body;
    }
}
