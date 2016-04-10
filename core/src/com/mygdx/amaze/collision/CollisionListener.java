package com.mygdx.amaze.collision;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.screens.PlayScreen;
import com.mygdx.amaze.networking.ItemRequest;
import com.mygdx.amaze.networking.MonsterChaseRequest;
import com.mygdx.amaze.networking.MonsterStopChaseRequest;
import com.mygdx.amaze.networking.RequestManager;

/**
 * Created by Randolph on 13/3/2016.
 */
public class CollisionListener implements ContactListener {

    public PlayScreen screen;

    private byte requestDelay = 1;

    // collision filter bits (for identification of collision entities)
    public static final short WALL_BIT              = 1 << 0;
    public static final short PLAYER_BIT            = 1 << 1;
    public static final short MONSTER_BIT           = 1 << 2;
    public static final short MONSTER_RADAR_BIT     = 1 << 3;
    public static final short MONSTER_BOUNDARY_BIT  = 1 << 4;
    public static final short ITEM_BIT              = 1 << 5;

    public CollisionListener(PlayScreen screen) {
        this.screen = screen;

        screen.world.setContactListener(this);
    }

    private void onItemCollision(Fixture fixtureA, Fixture fixtureB) {
        Item item;

        if (fixtureA.getFilterData().categoryBits == ITEM_BIT) {
            item = (Item) fixtureA.getUserData();
        } else {
            item = (Item) fixtureB.getUserData();
        }

        if (AmazeGame.SINGLE_PLAYER) {
            screen.player.obtainItem(item);
        } else {
            RequestManager.getInstance().newRequest(new ItemRequest(screen.player, item));
        }
    }

    private void onMonsterCollision(Fixture fixtureA, Fixture fixtureB) {
        if (!screen.player.shielded) {
            screen.player.attacked = true;
        }
    }

    private void onMonsterRadarCollision(Fixture fixtureA, Fixture fixtureB) {
        Monster monster;
        short categoryBit = fixtureA.getFilterData().categoryBits;

        if (categoryBit == PLAYER_BIT) {
            monster = (Monster) fixtureB.getUserData();
        } else {
            monster = (Monster) fixtureA.getUserData();
        }
        
        // request server for permission for monster to chase local player
        if (!monster.isChasing()) {
            if (AmazeGame.SINGLE_PLAYER) {
                monster.startChase(screen.player);
            } else {
                if (requestDelay == 1) {
                    RequestManager.getInstance().newRequest(new MonsterChaseRequest(screen.player, monster));
                }
                // delay the request so as not the flood the server?
                // send request at every 5th attempt
                requestDelay <<= 1;
                if (requestDelay == 32) requestDelay = 1;
            }
        }
        // prevent collided bodies from sleeping and thus missing a collision!
        monster.getBody().setAwake(true);
        screen.player.getBody().setAwake(true);
    }

    private void onMonsterRadarCollisionEnded(Fixture monsterFixture) {
        Monster monster = (Monster) monsterFixture.getUserData();

        // request server for permission for monster to stop chasing local player
        if (monster.isChasing()) {
            if (AmazeGame.SINGLE_PLAYER) {
                monster.stopChase();
            } else {
                RequestManager.getInstance().newRequest(new MonsterStopChaseRequest(monster));
            }
        }
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
            //onMonsterRadarCollision(fixtureA, fixtureB);
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

        switch (collidedEntities) {
        case PLAYER_BIT | MONSTER_BIT:
            contact.setEnabled(false); // allow player and monster to move through each other
        case PLAYER_BIT | MONSTER_RADAR_BIT:
            contact.setEnabled(false); // allow player and monster radar to move through each other
            onMonsterRadarCollision(fixtureA, fixtureB);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


}
