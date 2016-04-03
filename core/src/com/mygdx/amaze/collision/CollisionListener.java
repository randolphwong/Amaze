package com.mygdx.amaze.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 13/3/2016.
 */
public class CollisionListener implements ContactListener {

    public PlayScreen screen;

    // collision filter bits (for identification of collision entities)
    public static final short WALL_BIT              = 1 << 0;
    public static final short PLAYER_BIT            = 1 << 1;
    public static final short MONSTER_BIT           = 1 << 2;
    public static final short MONSTER_RADAR_BIT     = 1 << 3;
    public static final short MONSTER_BOUNDARY_BIT  = 1 << 4;
    public static final short ITEM_BIT              = 1 << 5;

    PlayScreen screen;

    public CollisionListener(PlayScreen screen) {
        this.screen = screen;

        screen.world.setContactListener(this);
    }

    private void onItemCollision(Fixture fixtureA, Fixture fixtureB) {
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        Body potionitemBody = screen.healthPotion.getBody();
        Body shielditemBody = screen.shield.getBody();

        if ((bodyA == potionitemBody) || (bodyB == potionitemBody)) {
            if(screen.player.health < 99) {
                screen.player.health = 99;
            }
            System.out.println("health: " + screen.player.health);
            screen.healthPotion.destroy();
        } else if ((bodyA == shielditemBody) || (bodyB == shielditemBody)) {
            screen.player.shielded = true;
            System.out.println("Shield obtained");
            screen.shield.destroy();
        }
    }

    private void onMonsterCollision(Fixture fixtureA, Fixture fixtureB) {
        if (!screen.player.shielded) {
            screen.player.attacked = true;
        }
    }

    private void onMonsterRadarCollision(Fixture fixtureA, Fixture fixtureB) {
        Monster monster;

        if (fixtureA.getFilterData().categoryBits == MONSTER_RADAR_BIT) {
            monster = (Monster) fixtureA.getUserData();
        } else {
            monster = (Monster) fixtureB.getUserData();
        }
        monster.startChase(screen.player);
    }

    private void onMonsterRadarCollisionEnded(Fixture monsterFixture) {
        ((Monster) monsterFixture.getUserData()).stopChase();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collidedEntities = fixtureA.getFilterData().categoryBits |
                               fixtureB.getFilterData().categoryBits;

        switch (collidedEntities) {
        case PLAYER_BIT | ITEM_BIT:
            onItemCollision(fixtureA, fixtureB);
            break;
        case PLAYER_BIT | MONSTER_BIT:
            onMonsterCollision(fixtureA, fixtureB);
            break;
        case PLAYER_BIT | MONSTER_RADAR_BIT:
            onMonsterRadarCollision(fixtureA, fixtureB);
            break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int categoryA = fixtureA.getFilterData().categoryBits;
        int categoryB = fixtureB.getFilterData().categoryBits;

        switch (categoryA | categoryB) {
        case PLAYER_BIT | MONSTER_BIT:
            screen.player.shielded = false;
            screen.player.attacked = false;
            break;
        case PLAYER_BIT | MONSTER_RADAR_BIT:
            onMonsterRadarCollisionEnded(categoryA == MONSTER_RADAR_BIT ? fixtureA : fixtureB);
            break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collidedEntities = fixtureA.getFilterData().categoryBits |
                               fixtureB.getFilterData().categoryBits;

        if (collidedEntities == (PLAYER_BIT | MONSTER_BIT)) {
            contact.setEnabled(false); // allow player and monster to move through each other
            if (!screen.player.shielded) {
                screen.player.health -= 0.5f;
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}
