package com.mygdx.amaze.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.networking.GameData;

/**
 * Created by Randolph on 13/3/2016.
 */
public class FriendInputComponent {

    private Friend friend;

    private Hud hud;

    public FriendInputComponent(Friend friend) {
        this.friend = friend;
    }

    public void update(float delta, GameData gameData) {
        if (gameData != null) {
            friend.x = gameData.playerPosition.x;
            friend.y = gameData.playerPosition.y;
        }
    }
}
