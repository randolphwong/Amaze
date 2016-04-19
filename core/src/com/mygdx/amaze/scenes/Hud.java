package com.mygdx.amaze.scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.amaze.components.Earthquake;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.screens.PlayScreen;


/**
 * Created by Randolph on 13/3/2016.
 */
public class Hud implements Disposable {

    public static float centerOfLeftGutter;
    public static float centerOfRightGutter;
    public static float gutterWidth;

    //time
    public Integer timer;
    private float timeCount;
    private boolean timeUp;
    private Label countdownLabel, timeLabel;
    public Table table;

    private SpriteBatch batch;

    private PlayScreen playScreen;

    public Stage stage;

    private Viewport viewport;

    private Touchpad touchpad;
    private ImageButton firebutton;
    private Image gunImage;

    private Sprite touchpadBackground, touchpadKnob;

    private Healthbar healthbar;
    private InventoryTest inventory;

    //earthquake
    public Earthquake earthquake;

    public Hud(SpriteBatch batch, PlayScreen playScreen) {
        this.batch = batch;
        this.playScreen =playScreen;
        // define the constants for the left and right gutters/pane
        gutterWidth = (Gdx.graphics.getWidth() - Gdx.graphics.getHeight()) / 2;
        centerOfLeftGutter = gutterWidth / 2;
        centerOfRightGutter = (1.5f * gutterWidth) + Gdx.graphics.getHeight();

        viewport = new ScreenViewport(new OrthographicCamera());
        stage = new Stage(viewport, batch);

        // touchpad
        makeTouchpad();

        // healthbar
        healthbar = new Healthbar(centerOfRightGutter, Gdx.graphics.getHeight() * 0.8f);
        stage.addActor(healthbar);

        //firebutton
        makeFirebutton();

        //playscreen
        this.playScreen = playScreen;

        // input
        Gdx.input.setInputProcessor(stage);

        //timer
        if(playScreen.level == 1){timer = 200;}
        else{timer = 300;}
        timeCount = 0;
        countdownLabel = new Label(String.format("Time: %2d", timer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        countdownLabel.setFontScale(4.5f);

        //define a table used to organize hud's labels
        table = new Table();
        table.top();
        table.setFillParent(true);

        //add labels to table
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(countdownLabel).expandX();

        //add table to the stage
        stage.addActor(table);




        //earthquake
        earthquake = new Earthquake();
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
        touchpad.setPosition(centerOfLeftGutter - (touchpadBackground.getWidth() / 2), Gdx.graphics.getHeight() * 0.1f);

        stage.addActor(touchpad);
    }

    public void makeFirebutton() {
        Sprite actor = new Sprite(new Texture(Gdx.files.internal("hud/orangebutton.png")));
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new Sprite(actor));
        firebutton = new ImageButton(imageButtonStyle);
        firebutton.setSize(gutterWidth/2,gutterWidth/2);
        firebutton.setPosition(centerOfRightGutter - (firebutton.getWidth() / 2), Gdx.graphics.getHeight() * 0.1f);
        stage.addActor(firebutton);

        gunImage = new Image(new Texture(Gdx.files.internal("item/LaserGun.png")));
        gunImage.setSize(firebutton.getWidth() * 0.8f, firebutton.getHeight() * 0.8f);
        gunImage.setPosition(centerOfRightGutter - (gunImage.getWidth() / 2), 
                (firebutton.getY() + (firebutton.getHeight() / 2) - (gunImage.getHeight() / 2)));
        gunImage.setVisible(false);
        stage.addActor(gunImage);
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void update(float delta){
        if(playScreen.player.gunequipped){
            gunImage.setVisible(true);
        }
        timeCount += delta;
        if (timeCount >= 1) {
            if (timer > 0) {
                timer--;
                if (timer == 200 || timer == 150 || timer == 125 ||
                        timer == 100 || timer == 75 || timer == 50 ||
                        timer == 25 || timer == 12 || timer == 10 ||
                        timer == 5){
                    earthquake.rumble(15.0f, 3f);
                    Gdx.input.vibrate(2700);
                }
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("TIME: %2d", timer));
            timeCount = 0;
        }
    }


    public Touchpad getTouchpad() {
        return touchpad;
    }

    public Healthbar getHealthbar() {
        return healthbar;
    }

    public ImageButton getFirebutton() {
        return firebutton;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
