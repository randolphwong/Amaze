package com.mygdx.amaze.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Loo Yi on 3/28/2016.
 */
public class PotionItemCollisionListener implements ContactListener{
    PlayScreen screen;

    public PotionItemCollisionListener(PlayScreen screen) {
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
        Body itemBody = screen.healthPotion.getBody();

        if ((bodyA == playerBody && bodyB == itemBody) ||
                (bodyB == playerBody && bodyA == itemBody)) {
            if(screen.player.health < 99) {
                screen.player.health += 33;
            }
            System.out.println("health: " + screen.player.health);
            playerBody.setUserData(true);
            screen.healthPotion.destroy();
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
