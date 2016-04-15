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

    public Stage stage;

    private Viewport viewport;

    private Touchpad touchpad;
    private ImageButton firebutton;

    private Sprite touchpadBackground, touchpadKnob;

    private Healthbar healthbar;
    private InventoryTest inventory;

    //earthquake
    public Earthquake earthquake;

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
        healthbar = new Healthbar(centerOfRightGutter, Gdx.graphics.getHeight() * 0.1f);
        stage.addActor(healthbar);

        //firebutton
        makeFirebutton();

        // input
        Gdx.input.setInputProcessor(stage);

        //timer
        timer = 300;
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
    public void makeFirebutton(){
        Sprite actor = new Sprite(new Texture(Gdx.files.internal("hud/orangebutton.png")));
        actor.setSize(gutterWidth/2,gutterWidth/2);
        Sprite accept = new Sprite(new Texture(Gdx.files.internal("item/LaserGun.png")));
        accept.setSize(gutterWidth/2,gutterWidth/2);
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(new Sprite(actor));

        imageButtonStyle.imageUp = new TextureRegionDrawable(new Sprite(accept));
        firebutton = new ImageButton(imageButtonStyle);

        Table table = new Table();
        table.add(firebutton).size(gutterWidth/2,gutterWidth/2);
        table.setPosition(centerOfRightGutter,50);
        stage.addActor(table);
    }

    public boolean isTimeUp() { return timeUp; }

    public void update(float delta){
        timeCount += delta;
        if(timeCount >= 1){
            if (timer > 0) {
                timer--;
                if(timer == 200 || timer == 150 || timer == 125 ||
                        timer == 100 || timer == 75 || timer == 50 ||
                        timer == 25 || timer == 12 || timer == 10 ||
                        timer == 5){
                    earthquake.rumble(15.0f, 4f);
                }
            }
            else {
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

    public ImageButton getFirebutton() {return firebutton; }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
