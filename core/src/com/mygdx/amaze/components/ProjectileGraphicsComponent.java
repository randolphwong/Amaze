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

        switch (projectile.playerFaceState) {
        case UP:
        case DOWN:
            laserSprite = new Sprite(new Texture("Laser/laser-vertical.png"));
            laserSprite.setSize(Projectile.THICKNESS,Projectile.LENGTH);
            break;
        case LEFT:
        case RIGHT:
            laserSprite = new Sprite(new Texture("Laser/laser-horizontal.png"));
            laserSprite.setSize(Projectile.LENGTH,Projectile.THICKNESS);
            break;
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
