package com.mygdx.amaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.mygdx.amaze.screens.PlayScreen;
import com.mygdx.amaze.screens.MainMenuScreen;

public class AmazeGame extends Game {

    // the map is 1600 x 1600
    public static final float VIEW_WIDTH = 1600;
    public static final float VIEW_HEIGHT = 1600;

	public SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {

	}

	@Override
	public void render () {
        super.render();
	}
}
