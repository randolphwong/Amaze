package com.mygdx.amaze.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.utilities.ItemType;
import com.mygdx.amaze.entities.Monster;



/**
 * Created by Dhanya on 22/03/2016.
 */
public class ItemGraphicsComponent extends GraphicsComponent {
    private Item item;
    private ItemType type;

    //currently 3 items.
    private Sprite[] itemList = new Sprite[3];


    private Sprite itemSprite;


    public ItemGraphicsComponent(Item item) {

        itemList[2] = new Sprite(new Texture("item/Shield.png"));
        itemList[1] = new Sprite(new Texture("item/LaserGun.png"));
        itemList[0] = new Sprite(new Texture("item/Potion.png"));

        this.item = item;
        this.type = item.type;

        switch (type){
            case HEALTH_POTION:
                itemSprite = itemList[0];
                itemSprite.setSize(30,30);
                break;
            case LASER_GUN:
                itemSprite = itemList[1];
                itemSprite.setSize(40,40);
                break;
            case SHIELD:
                itemSprite = itemList[2];
                itemSprite.setSize(50,50);
                break;
        }


        itemSprite.setCenter(item.posX, item.posY);


    }

    @Override
    public void update(float delta) {
        itemSprite.setCenter(item.posX, item.posY);
    }

    @Override
    public void draw(SpriteBatch batch) {
        itemSprite.draw(batch);
    }

    @Override
    public void dispose() {
        for (int i = 0; i < itemList.length; i++)
            itemList[i].getTexture().dispose();
    }

    public Sprite getItemSprite() {
        return itemSprite;
    }
}
