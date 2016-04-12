package com.mygdx.amaze.components;

import com.mygdx.amaze.entities.Projectile;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Loo Yi on 4/8/2016.
 */
public class ProjectileInputComponent {

    private Projectile projectile;
    PlayScreen screen;

    public ProjectileInputComponent(Projectile projectile ,PlayScreen screen){
        this.projectile = projectile;
        this.screen = screen;

        switch (projectile.playerFaceState) {
        case UP: projectile.velocity.y = 500; break;
        case DOWN: projectile.velocity.y = -500; break;
        case LEFT: projectile.velocity.x = -500; break;
        case RIGHT: projectile.velocity.x = 500; break;
        }
    }
    public void update(float delta){

    }
}
