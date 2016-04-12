package com.mygdx.amaze.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Projectile;

import com.mygdx.amaze.collision.CollisionListener;

/**
 * Created by Loo Yi on 3/31/2016.
 */
public class ProjectilePhysicsComponent extends PhysicsComponent {
    private Projectile projectile;

    private World world;
    private Body body;

    public ProjectilePhysicsComponent(World world,Projectile projectile){
        this.world =world;
        this.projectile = projectile;

        createBody();
    }

    @Override
    public void update(float delta) {
        body.setLinearVelocity(projectile.velocity);
        projectile.x = body.getPosition().x;
        projectile.y = body.getPosition().y;
    }

    @Override
    public void createBody() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(projectile.x, projectile.y);
        body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(10);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;

        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = CollisionListener.PROJECTILE_BIT;
        fixtureDef.filter.maskBits = CollisionListener.WALL_BIT |
                                     CollisionListener.MONSTER_BIT;

        body.createFixture(fixtureDef).setUserData(projectile);
        circleShape.dispose();

    }

    @Override
    public Body getBody() {
        return body;
    }
}
