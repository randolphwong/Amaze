package com.mygdx.amaze.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.amaze.entities.Projectile;
import com.mygdx.amaze.screens.PlayScreen;

import java.util.ArrayList;

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
        Body shielditemBody = screen.shield.getBody();
        Body lasergunBody = screen.laserGun.getBody();

        if ((bodyA == playerBody && bodyB == monsterBody) ||
                (bodyB == playerBody && bodyA == monsterBody)) {
            if(!screen.player.shielded) {
                screen.player.health -= 33;
            }else{
                screen.player.shielded = false;
            }
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
        }else if ((bodyA == playerBody && bodyB == shielditemBody) ||
                (bodyB == playerBody && bodyA == shielditemBody)) {
            screen.player.shielded = true;
            System.out.println("Shield obtained");
            playerBody.setUserData(true);
            screen.shield.destroy();
        }else if ((bodyA == playerBody && bodyB == lasergunBody) ||
                (bodyB == playerBody && bodyA == lasergunBody)) {
            screen.player.gunequipped = true;
            screen.player.shotsLeft+=5;
            System.out.println("Laser gun obtained");
            playerBody.setUserData(true);
            screen.laserGun.destroy();
        }
        for(Projectile projectile : screen.projectiles){
            if ((bodyA == monsterBody && bodyB == projectile.getBody()) ||
                    (bodyB == monsterBody && bodyA == projectile.getBody())) {
                projectile.destroy();
                screen.monster.destroy();
            }
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
        for(Projectile projectile : screen.projectiles){
            if ((bodyA == playerBody && bodyB == projectile.getBody()) ||
                    (bodyB == playerBody && bodyA == projectile.getBody())) {
               contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
