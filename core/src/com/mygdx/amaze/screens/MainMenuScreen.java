package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.networking.AmazeNetworkListener;
import com.mygdx.amaze.networking.GameData;

public class MainMenuScreen implements Screen, AmazeNetworkListener {

    private AmazeGame game;
    
    // stage ui (buttons and etc.)
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture;
    private TextButton textButton;

    private boolean joinedRoom; // guarded by MainMenuScreen.class
    private GameData gameData; // guarded by MainMenuScreen.class

    public MainMenuScreen(AmazeGame game) {
        this.game = game;

        // set up a stage for displaying button
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // add a button
        textButton = createButton();
        stage.addActor(textButton);

        // set up networking
        game.networkClient.setNetworkListener(this);
        joinedRoom = false;
    }

    public TextButton createButton() {
        font = new BitmapFont(Gdx.files.internal("hud/lobster.fnt"));
        buttonTexture = new Texture(Gdx.files.internal("hud/button.png"));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextButton textButton = new TextButton("START GAME", buttonStyle);
        textButton.setPosition((Gdx.graphics.getWidth() / 2) - (textButton.getWidth() / 2), (Gdx.graphics.getHeight() / 2) - (textButton.getHeight() / 2));
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
        game.networkClient.joinRoom();
    }

    public void update(float delta) {
        synchronized(MainMenuScreen.class) {
            if (joinedRoom) {
                joinedRoom = false;

                game.setScreen(new PlayScreen(game, gameData.player, 1));
                dispose();
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void onRoomCreated(GameData data) {
        // unfortunately i cannot perform setscreen here because this is not
        // under the UI thread... so it means this onRoomCreated is kind of
        // redundant
        synchronized(MainMenuScreen.class) {
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
