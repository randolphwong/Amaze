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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.amaze.AmazeGame;

/**
 * Created by Dhanya on 20/04/2016.
 */
public class ClientDisconnectedScreen implements Screen {

    private AmazeGame game;

    // stage ui (buttons and etc.)
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture, backgroundImg;
    private Sprite backgroundSet;
    private TextButton textButton;
    private PlayScreen playScreen;
    private Label pointsLabel, clientOut;
    private Table table;
    private int points;



    public ClientDisconnectedScreen(AmazeGame game, PlayScreen playScreen, int points) {
        this.game = game;
        this.playScreen = playScreen;
        this.points = points;

        // set up a stage for displaying button
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.input.setInputProcessor(stage);

        backgroundImg = new Texture(Gdx.files.internal("startScreen/winScreen.png"));
        backgroundSet = new Sprite(backgroundImg);

        clientOut = new Label(String.format("CLIENT DISCONNECTED"), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("hud/SF_Atarian.fnt")), Color.WHITE));
        clientOut.setFontScale(3.8f);
        clientOut.setPosition(Gdx.graphics.getWidth() / 2 - clientOut.getWidth() / 2, Gdx.graphics.getHeight() / 2 - clientOut.getHeight());



        pointsLabel = new Label(String.format("POINTS: %2d", points), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("hud/SF_Atarian.fnt")), Color.WHITE));
        pointsLabel.setFontScale(5.5f);
        pointsLabel.setPosition(Gdx.graphics.getWidth()/2 - pointsLabel.getWidth()/2, Gdx.graphics.getHeight()/2-pointsLabel.getHeight()-clientOut.getHeight());

        //define a table used to organize hud's labels
        table = new Table();
        table.top();
        table.setFillParent(true);

        //add labels to table
        table.row();
        table.row();
        table.add(clientOut).expandX().pad(70);

        //add table to the stage
        stage.addActor(table);


        // add a button
        textButton = createButton();
        stage.addActor(textButton);


    }

    public TextButton createButton() {
        font = new BitmapFont(Gdx.files.internal("hud/SF_Atarian.fnt"));
        buttonTexture = new Texture(250, 60, Pixmap.Format.Alpha);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextButton textButton = new TextButton("PLAY AGAIN", buttonStyle);
        textButton.setSize(Gdx.graphics.getWidth()/64, Gdx.graphics.getHeight()/64);
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
        game.setScreen(new SplashScreen(game));
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

