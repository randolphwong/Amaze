package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.amaze.entities.Projectile;
import com.mygdx.amaze.screens.PlayScreen;

/**
 * Created by Loo Yi on 3/31/2016.
 */
public class ProjectileGraphicsComponent extends GraphicsComponent {
    private Projectile projectile;

    private World world;

    private Sprite laserSprite;

    public ProjectileGraphicsComponent(Projectile projectile,PlayScreen screen) {

        this.projectile = projectile;
        if(screen.player.faceState == 0 || screen.player.faceState ==2){
            laserSprite = new Sprite(new Texture("Laser/laser-vertical.png"));
            laserSprite.setSize(Projectile.THICKNESS,Projectile.LENGTH);
        }else if(screen.player.faceState == 1 || screen.player.faceState == 3){
            laserSprite = new Sprite(new Texture("Laser/laser-horizontal.png"));
            laserSprite.setSize(Projectile.LENGTH,Projectile.THICKNESS);
        }
//        laserSprite.setCenter(projectile.x,projectile.y);


    }

    @Override
    public void update(float delta) {
        laserSprite.setCenter(projectile.x,projectile.y);
    }

    @Override
    public void draw(SpriteBatch batch) {
        laserSprite.draw(batch);
    }

    public void dispose(){laserSprite.getTexture().dispose();}
}
