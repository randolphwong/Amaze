package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.amaze.AmazeGame;

/**
 * Created by Dhanya on 09/04/2016.
 */
public class SplashScreen implements Screen{

    private AmazeGame game;

    // stage ui
    private Stage stage;
    private BitmapFont font;
    private Texture backgroundImg;
    private Sprite backgroundSet;

    private long time;


    public SplashScreen(AmazeGame game) {
        this.game = game;
        time = TimeUtils.millis();
        System.out.println("time is: " + time);
        // set up a stage for displaying button
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        backgroundImg = new Texture(Gdx.files.internal("startScreen/splashScreen_1.png"));
        backgroundSet = new Sprite(backgroundImg);
        backgroundSet.setScale(Gdx.graphics.getWidth() / backgroundImg.getWidth(), Gdx.graphics.getHeight() / backgroundSet.getHeight());



    }

    public void update(float delta) {

    }



    @Override
    public void render(float delta) {
        update(delta);
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();

        if (TimeUtils.millis()>time + 5000) {
            game.setScreen(new MainScreen(game));
            System.out.println("GOING TO MAIN SCREEN");
        }
    }



    @Override
    public void dispose() {
        font.dispose();
        stage.dispose();
        backgroundImg.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // centre the camera on the stage on resize
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

}

