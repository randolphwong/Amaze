package com.mygdx.amaze;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.mygdx.amaze.util.MapBodyBuilder;
import com.mygdx.amaze.entities.Player;

public class Amaze implements ApplicationListener {
    
    private OrthographicCamera camera;
    private Stage stage;
    private Touchpad touchpad;
    private Skin touchpadSkin;
    private TouchpadStyle touchpadStyle;
	private SpriteBatch batch;
    private Drawable touchBackground;
    private Drawable touchKnob;
    private Viewport viewport;
    private Viewport stageviewport;

    // map
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    // physics
    private World world;
    private Body body;
    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    // player
    private Player rey;

    public static final float WORLD_WIDTH = 810;
    public static final float WORLD_HEIGHT = 600;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		tiledMap = new TmxMapLoader().load("myMaze_withwall.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        camera = new OrthographicCamera();
        viewport = new FitViewport(300, 300, camera);
        stageviewport = new FitViewport(900, 900, new OrthographicCamera());
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

		//Create a touchpad skin	
		touchpadSkin = new Skin();
		//Set background image
		touchpadSkin.add("touchBackground", new Texture("touchBackground.png"));
		//Set knob image
		touchpadSkin.add("touchKnob", new Texture("touchKnob.png"));
		//Create TouchPad Style
		touchpadStyle = new TouchpadStyle();
		//Create Drawable's from TouchPad skin
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		//Apply the Drawables to the TouchPad Style
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		//Create new TouchPad with the created style
		touchpad = new Touchpad(10, touchpadStyle);
		//setBounds(x,y,width,height)
		touchpad.setBounds(0, 100, 150, 150);

		stage = new Stage(stageviewport, batch);
		stage.addActor(touchpad);			
		Gdx.input.setInputProcessor(stage);


        // physics
        // create the physics world public World(Vector2 gravity, boolean doSleep)
        world = new World(new Vector2(0, 0), true);

        rey = new Player(this);

        // as name suggests...
        debugRenderer = new Box2DDebugRenderer();

        Array<Body> wallBodies = MapBodyBuilder.buildShapes("wall", tiledMap, 1, world);

	}

    public World getWorld() {
        return world;
    }

    public void handleInput() {
        //if (Gdx.input.justTouched()) {
            //rey.setY(rey.getY() + 30);
        //}
    }

    public void update() {

        if (Math.abs(touchpad.getKnobPercentX()) > Math.abs(touchpad.getKnobPercentY())) {
            rey.move(touchpad.getKnobPercentX() * 100, 0);
        } else {
            rey.move(0, touchpad.getKnobPercentY() * 100);
        }
        rey.update();
        
        camera.position.x = rey.getX();
        camera.position.y = rey.getY();

        /*
         *public void step(float timeStep, int velocityIterations, int
         *        positionIterations)
         *    Take a time step. This performs collision detection, integration,
         *         and constraint solution.
         */
        //Why 6 and 2?  ‘cause that’s what the LibGDX site recommend and that works
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

	@Override
	public void render () {
        handleInput();
        update();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy();

		batch.begin();
        rey.draw(batch);
		batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        debugRenderer.render(world, debugMatrix);

	}

	@Override
	public void dispose () {
        world.dispose();
        rey.dispose();
	}

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}

	@Override
	public void resize (int width, int height) {
        viewport.update(width, height);
        stageviewport.update(width, height);
	}
}

