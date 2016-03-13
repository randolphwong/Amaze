package com.mygdx.amaze.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.amaze.AmazeGame;

/**
 * Created by Randolph on 13/3/2016.
 */
public class Hud implements Disposable {

    private SpriteBatch batch;

    public Stage stage;

    public Viewport viewport;

    private Touchpad touchpad;

    private Sprite touchpadBackground, touchpadKnob;

    public Hud(SpriteBatch batch) {
        this.batch = batch;

        viewport = new FitViewport(AmazeGame.VIEW_WIDTH / 3, AmazeGame.VIEW_HEIGHT / 4, new OrthographicCamera());
        viewport.apply();

        stage = new Stage(viewport, batch);

        touchpadBackground = new Sprite(new Texture(Gdx.files.internal("hud/touchpad_background.png")));
        touchpadKnob = new Sprite(new Texture(Gdx.files.internal("hud/touchpad_knob.png")));

        // magic numbers
        touchpadBackground.setSize(60, 60);
        touchpadKnob.setSize(30, 30);

        Touchpad.TouchpadStyle ts = new Touchpad.TouchpadStyle();
        ts.background = new SpriteDrawable(touchpadBackground);
        ts.knob = new SpriteDrawable(touchpadKnob);

        // magic numbers
        touchpad = new Touchpad(5f, ts);
        touchpad.setPosition(30, 30);

        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
