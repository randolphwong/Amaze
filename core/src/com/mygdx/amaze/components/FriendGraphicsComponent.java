package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.AbstractPlayer;
import com.mygdx.amaze.utilities.Const;

/**
 * Created by Randolph on 13/3/2016.
 */
public class FriendGraphicsComponent extends AbstractPlayerGraphicsComponent {

    private float previousX;
    private float previousY;

    public FriendGraphicsComponent(AbstractPlayer player) {
        super(player);
        setPlayerType(player.getType() == Const.MASTER_CLIENT ? new String("friend") : new String("player"));
        setupSprite();
        setUpAnimation();
        System.out.println("friend type: " + playerType);
        previousX = player.x;
        previousY = player.y;
    }

    @Override
    protected void updateMovementState() {
        if (player.dead) {
            movementState = MovementState.STATIONARY;
            return;
        }
        if (player.x < previousX) {
            movementState = MovementState.MOVE_LEFT;
        } else if (player.x > previousX) {
            movementState = MovementState.MOVE_RIGHT;
        } else if (player.y < previousY) {
            movementState = MovementState.MOVE_DOWN;
        } else if (player.y > previousY) {
            movementState = MovementState.MOVE_UP;
        } else {
            movementState = MovementState.STATIONARY;
        }
        previousX = player.x;
        previousY = player.y;
    }
}
