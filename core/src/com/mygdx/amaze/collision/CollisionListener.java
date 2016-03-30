package com.mygdx.amaze.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 13/3/2016.
 */
public class CollisionListener implements ContactListener {

    PlayScreen screen;

    public CollisionListener(PlayScreen screen) {
        this.screen = screen;
        screen.world.setContactListener(this);
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();


        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Body playerBody = screen.player.getBody();
        Body monsterBody = screen.monster.getBody();
        Body potionitemBody = screen.healthPotion.getBody();


        if ((bodyA == playerBody && bodyB == monsterBody) ||
                (bodyB == playerBody && bodyA == monsterBody)) {
            screen.player.health -= 33;
            System.out.println("health: " + screen.player.health);
            playerBody.setUserData(true);
        }else if ((bodyA == playerBody && bodyB == potionitemBody) ||
                (bodyB == playerBody && bodyA == potionitemBody)) {
            if(screen.player.health < 99) {
                screen.player.health += 33;
            }
            System.out.println("health: " + screen.player.health);
            playerBody.setUserData(true);
            screen.healthPotion.destroy();
        }

        //Check if player picks up item


    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Body playerBody = screen.player.getBody();
        Body monsterBody = screen.monster.getBody();

        if ((bodyA == playerBody && bodyB == monsterBody) ||
                (bodyB == playerBody && bodyA == monsterBody)) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
