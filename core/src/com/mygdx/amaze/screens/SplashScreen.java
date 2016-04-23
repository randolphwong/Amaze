package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.amaze.AmazeGame;

/**
 * Created by Dhanya on 09/04/2016.
 */
public class SplashScreen implements Screen{

    private AmazeGame game;

    public OrthographicCamera camera;
    public Viewport viewport;

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
        camera = new OrthographicCamera();
        viewport = new FitViewport(500, 500, camera);
        camera.position.x = 250;
        camera.position.y = 250;

        backgroundImg = new Texture(Gdx.files.internal("startScreen/splashScreen_2.png"));
        backgroundSet = new Sprite(backgroundImg);
        backgroundSet.setSize(500, 500);
    }

    public void update(float delta) {
        camera.update();
    }



    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        backgroundSet.draw(game.batch);
        game.batch.end();

        if (TimeUtils.millis()>time + 1000) {
            game.setScreen(new MainScreen(game));
            System.out.println("GOING TO MAIN SCREEN");
        }
    }



    @Override
    public void dispose() {
        font.dispose();
        //stage.dispose();
        backgroundImg.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // centre the camera on the stage on resize
        //stage.getViewport().update(width, height, true);
        viewport.update(width, height);
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

