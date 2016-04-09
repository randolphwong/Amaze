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
    }
    public void update(float delta){
        if(screen.player.faceState == 0){
            projectile.velocity.y = 500;
            projectile.velocity.x = 0;
        }else if(screen.player.faceState == 1){
            projectile.velocity.x = 500;
            projectile.velocity.y = 0;
        }else if(screen.player.faceState == 2){
            projectile.velocity.y = -500;
            projectile.velocity.x = 0;
        }else if(screen.player.faceState == 3){
            projectile.velocity.x = -500;
            projectile.velocity.y = 0;
        }
    }
}
