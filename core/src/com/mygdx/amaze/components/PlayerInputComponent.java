package com.mygdx.amaze.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygdx.amaze.entities.Item;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.entities.Player.FaceState;
import com.mygdx.amaze.entities.Projectile;
import com.mygdx.amaze.scenes.Hud;

/**
 * Created by Randolph on 13/3/2016.
 */
public class PlayerInputComponent {

    private Player player;
    private Sound sound_fire = Gdx.audio.newSound(Gdx.files.internal("sound/firesoundeffect.ogg"));
    private Sound sound_hit = Gdx.audio.newSound(Gdx.files.internal("sound/takingdamage.mp3"));
    private Sound sound_gunpickup = Gdx.audio.newSound(Gdx.files.internal("sound/gunpickupsound.mp3"));
    private Sound sound_potionpickup = Gdx.audio.newSound(Gdx.files.internal("sound/potionsound.mp3"));
    private Sound sound_shieldpickup = Gdx.audio.newSound(Gdx.files.internal("sound/shieldsound.mp3"));

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
                fireLaser();
            }
        });
    }

    public void obtainItem(Item item) {
        switch (item.type) {
        case HEALTH_POTION:
            if(player.health < 99) {
                player.health = 99;
                sound_potionpickup.play(0.5f);
            }
            break;
        case LASER_GUN:
            player.gunequipped = true;
            player.shotsLeft += 5;
            sound_gunpickup.play(0.5f);
            break;
        case SHIELD:
            player.shielded = true;
            sound_shieldpickup.play(0.5f);
            break;
        }
        item.destroy();
    }

    public void attacked() {
        player.attacked = true;
        sound_hit.play();
    }

    private void updateFaceState () {
        if(player.velocity.x>0 && player.velocity.y==0){
            player.faceState = FaceState.RIGHT;
        }else if(player.velocity.x<0 && player.velocity.y==0){
            player.faceState = FaceState.LEFT;
        }else if(player.velocity.x==0 && player.velocity.y>0){
            player.faceState = FaceState.UP;
        }else if(player.velocity.x ==0 && player.velocity.y<0) {
            player.faceState = FaceState.DOWN;
        }
    }

    private void fireLaser() {
        if (player.gunequipped) {
            sound_fire.play(0.5f);
            Projectile p = new Projectile(player.screen,player.x,player.y, player.faceState);
            player.screen.projectiles.add(p);
            player.shotsDone += 1;
            player.shotsLeft--;
            if (player.shotsLeft <= 0) {
                player.gunequipped = false;
            }
        }
    }

    private void updateVelocity() {
        Vector2 newVelocity = new Vector2(0, 0);

        if (Math.abs(touchpad.getKnobPercentX()) > Math.abs(touchpad.getKnobPercentY())) {
            newVelocity.x = touchpad.getKnobPercentX() * 100;
        } else {
            newVelocity.y = touchpad.getKnobPercentY() * 100;
        }

        player.velocity.set(newVelocity);

    }

    private void updateHealth() {
        if (player.attacked && !player.shielded) {
            player.health -= 0.5f;
        }
    }

    public void update(float delta) {
        updateVelocity();
        updateHealth();
        updateFaceState();
    }

    public void dispose() {
        sound_hit.dispose();
        sound_fire.dispose();
        sound_gunpickup.dispose();
        sound_shieldpickup.dispose();
        sound_potionpickup.dispose();
    }
}
