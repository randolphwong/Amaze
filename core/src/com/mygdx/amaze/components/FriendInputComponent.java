package com.mygdx.amaze.components;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.entities.Projectile;
import com.mygdx.amaze.entities.Player.FaceState;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Randolph on 13/3/2016.
 */
public class FriendInputComponent {

    public static final float RESPAWN_TIME = 2; // time in seconds
    private Friend friend;
    private PlayScreen screen;

    private float respawnTimer;

    public FriendInputComponent(Friend friend, PlayScreen screen) {
        this.friend = friend;
        this.screen = screen;
    }

    public void update(float delta, NetworkData networkData) {
        if (networkData.isAvailable()) {
            if (networkData.playerPosition() != null) {
                friend.targetX = networkData.playerPosition().x;
                friend.targetY = networkData.playerPosition().y;
                friend.attacked = networkData.isPlayerAttacked();
                friend.shielded = networkData.isPlayerShielded();
                //friend.faceState = networkData.playerFaceState();
            }
            if (networkData.isPlayerShooting()) {
                Projectile p = new Projectile(this.screen,friend.x,friend.y,networkData.playerFaceState());
                this.screen.projectiles.add(p);
            }

            if (networkData.isPlayerDead()) {
                friend.dead = true;
            }

            if (friend.dead) {
                respawnTimer += delta;
                if (respawnTimer >= RESPAWN_TIME) {
                    friend.dead = false;
                }
            }
        }

    }
}
