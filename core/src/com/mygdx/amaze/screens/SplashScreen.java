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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.networking.GameData;

/**
 * Created by Dhanya on 04/04/2016.
 */
public class SplashScreen implements Screen {
    private AmazeGame game;

    // stage ui (buttons and etc.)
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture, backgroundImg;
    private Sprite backgroundSet;
    private TextButton textButton;

    private int time = 0;


    public SplashScreen(AmazeGame game) {
        this.game = game;

        // set up a stage for displaying button
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

//        backgroundImg = new Texture(Gdx.files.internal("startScreen/instructions.png"));
//        backgroundSet = new Sprite(backgroundImg);
//        backgroundSet.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // add a button
        textButton = createButton();
        stage.addActor(textButton);


    }

    public TextButton createButton() {
        font = new BitmapFont(Gdx.files.internal("hud/lobster.fnt"));
        buttonTexture = new Texture(Gdx.files.internal("hud/button.png"));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextButton textButton = new TextButton("START GAME", buttonStyle);
        textButton.setPosition(Gdx.graphics.getWidth()/2 - textButton.getWidth()/2, Gdx.graphics.getHeight()/2 - textButton.getHeight()/2);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClicked();
            }

            ;
        });

        return textButton;
    }

    public void buttonClicked() {
        game.setScreen(new MainMenuScreen(game));
    }

    public void update(float delta) {

        }


    @Override
    public void render(float delta) {
        update(delta);
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        game.batch.begin();
//        game.batch.draw(backgroundSet, stage.getWidth()/8, stage.getHeight()/8);
//        game.batch.end();

        stage.act(delta);
        stage.draw();
    }



    @Override
    public void dispose() {
        buttonTexture.dispose();
        font.dispose();
        stage.dispose();
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
