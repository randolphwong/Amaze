//package com.mygdx.amaze.screens;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.scenes.scene2d.InputEvent;
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
//import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
//import com.badlogic.gdx.utils.viewport.FillViewport;
//import com.badlogic.gdx.utils.viewport.ScreenViewport;
//
//import com.mygdx.amaze.AmazeGame;
//import com.mygdx.amaze.networking.AmazeNetworkListener;
//import com.mygdx.amaze.networking.GameData;
//import com.mygdx.amaze.utilities.Const;
//
//public class InstructionScreen implements Screen, AmazeNetworkListener {
//
//    private AmazeGame game;
//
//    // stage ui (buttons and etc.)
//    private Stage stage;
//    private BitmapFont font;
//    private Texture buttonTexture, backgroundImg;
//    private Sprite backgroundSet;
//    private TextButton textButton;
//
//    private boolean joinedRoom; // guarded by InstructionScreen.class
//    private GameData gameData; // guarded by InstructionScreen.class
//
//    public InstructionScreen(AmazeGame game) {
//        this.game = game;
//
//        // set up a stage for displaying button
//        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
//
//        Gdx.input.setInputProcessor(stage);
//
//        backgroundImg = new Texture(Gdx.files.internal("startScreen/rsz_instructions.png"));
//        backgroundSet = new Sprite(backgroundImg);
//
//        // add a button
//        textButton = createButton();
//        stage.addActor(textButton);
//
//        // set up networking
//        game.networkClient.setNetworkListener(this);
//        joinedRoom = false;
//    }
//
//    public TextButton createButton() {
//        font = new BitmapFont(Gdx.files.internal("hud/lobster.fnt"));
//        buttonTexture = new Texture(Gdx.files.internal("hud/button_new.png"));
//
//        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
//        buttonStyle.font = font;
//        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
//        TextButton textButton = new TextButton("START GAME", buttonStyle);
//        textButton.setPosition(Gdx.graphics.getWidth()- textButton.getWidth(), 10);
//        textButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                buttonClicked();
//            };
//        });
//
//        return textButton;
//    }
//
//    public void buttonClicked() {
//        textButton.setText("Waiting ...");
//        if (AmazeGame.SINGLE_PLAYER) {
//            game.setScreen(new PlayScreen(game, Const.MASTER_CLIENT, 1));
//        } else {
//            game.networkClient.joinRoom();
//        }
//    }
//
//    public void update(float delta) {
//        //System.out.println("Width: " + Gdx.graphics.getWidth() + " Height: " + Gdx.graphics.getHeight());
//        synchronized(InstructionScreen.class) {
//            if (joinedRoom) {
//                joinedRoom = false;
//
//                game.setScreen(new PlayScreen(game, gameData.clientType, 1));
//                dispose();
//            }
//        }
//    }
//
//    @Override
//    public void render(float delta) {
//        update(delta);
//        // clear the screen
//        Gdx.gl.glClearColor(0, 0, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        game.batch.setProjectionMatrix(stage.getCamera().combined);
//        game.batch.begin();
//        game.batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        game.batch.end();
//
//        stage.act(delta);
//        stage.draw();
//    }
//
//    @Override
//    public void onRoomCreated(GameData data) {
//        // unfortunately i cannot perform setscreen here because this is not
//        // under the UI thread... so it means this onRoomCreated is kind of
//        // redundant
//        synchronized(InstructionScreen.class) {
//            joinedRoom = true;
//            gameData = data;
//        }
//    }
//
//    @Override
//    public void dispose() {
//        buttonTexture.dispose();
//        font.dispose();
//        stage.dispose();
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        // centre the camera on the stage on resize
//        stage.getViewport().update(width, height, true);
//    }
//
//    @Override
//    public void show() {
//
//    }
//
//    @Override
//    public void pause() {
//
//    }
//
//    @Override
//    public void resume() {
//
//    }
//
//    @Override
//    public void hide() {
//
//    }
//}

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.networking.AmazeNetworkListener;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.utilities.Const;

import java.text.Format;

public class InstructionScreen implements Screen, AmazeNetworkListener {

    private AmazeGame game;

    // stage ui (buttons and etc.)
    private Stage stage;
    private BitmapFont font;
    private Texture buttonTexture, backgroundImg;
    private Sprite backgroundSet;
    private TextButton textButton;

    private boolean joinedRoom; // guarded by InstructionScreen.class
    private GameData gameData; // guarded by InstructionScreen.class

    public InstructionScreen(AmazeGame game) {
        this.game = game;

        // set up a stage for displaying button
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.input.setInputProcessor(stage);

        backgroundImg = new Texture(Gdx.files.internal("startScreen/rsz_instructions.png"));
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
        textButton.setSize(300,60);
        textButton.setPosition(Gdx.graphics.getWidth()/2 + textButton.getWidth(), 45);
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

    public void update(float delta) {
        //System.out.println("Width: " + Gdx.graphics.getWidth() + " Height: " + Gdx.graphics.getHeight());
        synchronized(InstructionScreen.class) {
            if (joinedRoom) {
                joinedRoom = false;

                game.setScreen(new PlayScreen(game, gameData.clientType, 1));
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

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void onRoomCreated(GameData data) {
        // unfortunately i cannot perform setscreen here because this is not
        // under the UI thread... so it means this onRoomCreated is kind of
        // redundant
        synchronized(InstructionScreen.class) {
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
