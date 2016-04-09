package com.mygdx.amaze.components;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.scenes.Hud;

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerInputComponent extends InputComponent {

    private Player player;

    private Hud hud;
    private Touchpad touchpad;
    private ImageButton firebutton;

    public PlayerInputComponent(final Player player, Hud hud) {
        this.player = player;
        this.hud =hud;
        this.touchpad =hud.getTouchpad();
        this.firebutton = hud.getFirebutton();
        firebutton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Fire");
                player.fireLaser();
            }
        });
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
