package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.amaze.AmazeGame;

/**
 * Created by Dhanya on 04/04/2016.
 */
public class MainScreen implements Screen {
    private AmazeGame game;

    // stage ui (buttons and etc.)
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture, backgroundImg;
    private Sprite backgroundSet;
    private TextButton textButton;

    private int time = 0;


    public MainScreen(AmazeGame game) {
        this.game = game;

        // set up a stage for displaying button
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.input.setInputProcessor(stage);

        backgroundImg = new Texture(Gdx.files.internal("startScreen/rsz_startscreen.png"));
        backgroundSet = new Sprite(backgroundImg);
//        backgroundSet.setScale(Gdx.graphics.getWidth() / backgroundImg.getWidth(), Gdx.graphics.getHeight() / backgroundSet.getHeight());

//        backgroundSet.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        backgroundSet.setSize(backgroundImg.getWidth(), backgroundImg.getHeight());

        // add a button
        textButton = createButton();
        stage.addActor(textButton);


    }

    public TextButton createButton() {
        font = new BitmapFont(Gdx.files.internal("hud/SF_Atarian.fnt"));
//        buttonTexture = new Texture(Gdx.files.internal("hud/button_new.png"));
        buttonTexture = new Texture(250, 60, Pixmap.Format.Alpha);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.font.getData().setScale(Gdx.graphics.getWidth()/400, Gdx.graphics.getHeight()/320);
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextButton textButton = new TextButton("Press to Continue", buttonStyle);
        textButton.setPosition(Gdx.graphics.getWidth()/2 - textButton.getWidth()/2, Gdx.graphics.getHeight()/2 - 4*textButton.getHeight());
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
        game.setScreen(new InstructionScreen(game));
    }

    public void update(float delta) {

    }


    @Override
    public void render(float delta) {
        update(delta);
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

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
