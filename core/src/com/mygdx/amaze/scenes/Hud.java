package com.mygdx.amaze.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Randolph on 13/3/2016.
 */
public class Hud implements Disposable {

    private final float centerOfLeftGutter;
    private final float centerOfRightGutter;
    private final float gutterWidth;

    private SpriteBatch batch;

    public Stage stage;

    private Viewport viewport;

    private Touchpad touchpad;

    private Sprite touchpadBackground, touchpadKnob;

    private Healthbar healthbar;

    public Hud(SpriteBatch batch) {
        this.batch = batch;

        // define the constants for the left and right gutters/pane
        gutterWidth = (Gdx.graphics.getWidth() - Gdx.graphics.getHeight()) / 2;
        centerOfLeftGutter = gutterWidth / 2;
        centerOfRightGutter = (1.5f * gutterWidth) + Gdx.graphics.getHeight();

        viewport = new ScreenViewport(new OrthographicCamera());
        stage = new Stage(viewport, batch);

        // touchpad
        makeTouchpad();

        // healthbar
        healthbar = new Healthbar(centerOfRightGutter, 50);
        stage.addActor(healthbar);

        // input
        Gdx.input.setInputProcessor(stage);
    }

    public void makeTouchpad() {
        touchpadBackground = new Sprite(new Texture(Gdx.files.internal("hud/touchpad_background.png")));
        touchpadKnob = new Sprite(new Texture(Gdx.files.internal("hud/touchpad_knob.png")));

        // make the touchpad diameter to be half the gutter width
        touchpadBackground.setSize(gutterWidth / 2, gutterWidth / 2);
        touchpadKnob.setSize(touchpadBackground.getWidth() / 2, touchpadBackground.getHeight() / 2);

        Touchpad.TouchpadStyle ts = new Touchpad.TouchpadStyle();
        ts.background = new SpriteDrawable(touchpadBackground);
        ts.knob = new SpriteDrawable(touchpadKnob);

        // magic numbers
        touchpad = new Touchpad(5f, ts);
        touchpad.setPosition(centerOfLeftGutter - (touchpadBackground.getWidth() / 2), 30);

        stage.addActor(touchpad);
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
