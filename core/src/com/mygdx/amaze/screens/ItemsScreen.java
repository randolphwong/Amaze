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
import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.networking.AmazeNetworkListener;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.utilities.Const;

/**
 * Created by Dhanya on 18/04/2016.
 */

public class ItemsScreen implements Screen, AmazeNetworkListener {

    private AmazeGame game;

    // stage ui (buttons and etc.)
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture, backgroundImg;
    private Sprite backgroundSet;
    private TextButton textButton;

    private boolean joinedRoom; // guarded by this
    private GameData gameData; // guarded by this

    public ItemsScreen(AmazeGame game) {
        this.game = game;

        // set up a stage for displaying button
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.input.setInputProcessor(stage);

        backgroundImg = new Texture(Gdx.files.internal("startScreen/items.png"));
        backgroundSet = new Sprite(backgroundImg);

        // add a button
        textButton = createButton();
        stage.addActor(textButton);

        // set up networking
        game.networkClient.setNetworkListener(this);
        joinedRoom = false;
    }

    public TextButton createButton() {
        font = new BitmapFont(Gdx.files.internal("hud/SF_Atarian.fnt"));
        buttonTexture = new Texture(300, 60, Pixmap.Format.Alpha);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.RED;
        buttonStyle.font.getData().setScale(2.8f, 2.5f);
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextButton textButton = new TextButton("START GAME", buttonStyle);
        textButton.setSize(Gdx.graphics.getWidth()/512, Gdx.graphics.getHeight()/508);
        textButton.setPosition(4*Gdx.graphics.getWidth()/5, 45);
        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClicked();
            };
        });

        return textButton;
    }

    public void buttonClicked() {
        textButton.setText("Waiting ...");
        if (AmazeGame.SINGLE_PLAYER) {
            game.setScreen(new PlayScreen(game, Const.MASTER_CLIENT, 1));
        } else {
            game.networkClient.joinRoom();
        }
    }

    public synchronized void update(float delta) {
        if (joinedRoom) {
            joinedRoom = false;
            byte clientType = gameData.clientType;

            game.setScreen(new PlayScreen(game, clientType, 1));
            dispose();
        }
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
    public void onRoomCreated(GameData data) {
        synchronized(this) {
            joinedRoom = true;
            gameData = data;
        }
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
