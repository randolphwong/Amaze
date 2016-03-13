package com.mygdx.amaze.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.scenes.Hud;

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerInputComponent extends InputComponent {

    private Player player;

    private Hud hud;
    private Touchpad touchpad;

    public PlayerInputComponent(Player player) {
        this.player = player;
    }

    public void setHud(Hud hud) {
        this.hud = hud;
        touchpad = hud.getTouchpad();
    }

    public void update(float delta) {
        Vector2 newVelocity = new Vector2(0, 0);

        if (Math.abs(touchpad.getKnobPercentX()) > Math.abs(touchpad.getKnobPercentY())) {
            newVelocity.x = touchpad.getKnobPercentX() * 100;
        } else {
            newVelocity.y = touchpad.getKnobPercentY() * 100;
        }

        player.velocity.set(newVelocity);
    }
}
