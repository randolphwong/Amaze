package com.mygdx.amaze.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.utilities.Const;

/**
 * Created by Randolph on 13/3/2016.
 */
public class FriendInputComponent {

    private Friend friend;

    private Hud hud;

    public FriendInputComponent(Friend friend) {
        this.friend = friend;
    }

    public void update(float delta, NetworkData networkData) {
        if (networkData.isAvailable()) {
            friend.x = networkData.playerPosition().x;
            friend.y = networkData.playerPosition().y;
            friend.attacked = networkData.isPlayerAttacked();
            friend.shielded = networkData.isPlayerShielded();
        }
    }
}
