package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.amaze.entities.Monster;

/**
 * Created by Randolph on 13/3/2016.
 */
public class MonsterPhysicsComponent {

    private Monster monster;

    private World world;
    private Body body;

    public MonsterPhysicsComponent(Monster monster, World world) {
        this.monster = monster;
        this.world = world;

        createBody();
    }

    public void createBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(monster.WIDTH / 2, monster.HEIGHT / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        bodyDef.position.set(monster.spawnX, monster.spawnY);
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public void update(float delta) {
        body.setLinearVelocity(monster.velocity);
        monster.x = body.getPosition().x;
        monster.y = body.getPosition().y;
    }

    public Body getBody() {
        return body;
    }
}
