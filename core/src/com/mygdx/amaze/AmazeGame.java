package com.mygdx.amaze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.mygdx.amaze.networking.AmazeClient;
import com.mygdx.amaze.screens.PlayScreen;
import com.mygdx.amaze.screens.MainMenuScreen;

public class AmazeGame extends Game {

    // the map is 1600 x 1600
    public static final float VIEW_WIDTH = 1600;
    public static final float VIEW_HEIGHT = 1600;

    // networking
    public AmazeClient networkClient;

	public SpriteBatch batch;
	
	@Override
	public void create () {
        networkClient = new AmazeClient();
        try {
            networkClient.start();
        } catch (Exception e) {
            Gdx.app.error("AmazeGame", "networkClient.start()", e);
        }

		batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
	}

	@Override
	public void dispose() {
        networkClient.stop();
	}

	@Override
	public void render () {
        super.render();
	}
}
