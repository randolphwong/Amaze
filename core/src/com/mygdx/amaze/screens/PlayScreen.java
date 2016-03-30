package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.collision.CollisionListener;
import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.utilities.MapPhysicsBuilder;

/**
 * Created by Randolph on 12/3/2016.
 */
public class PlayScreen implements Screen {

    private AmazeGame game;

    // players
    public Player player;
    public Friend friend;
    public String playerType;

    public Monster monster;

    //Items
    public Item healthPotion;
    public Item laserGun;
    public Item shield;

    // camera and viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // box2d
    private Box2DDebugRenderer debugRenderer;
    public World world;
    private CollisionListener collisionListener;

//    //boundaries of the gate.
//    player 1: 485.98505 1495.758
//    player 2: 315.84088 1583.9951
    double leftBound = 315.5;
    double rightBound = 485.5;
    double topBound = 1495.5;
    double bottomBound = 1583.5;

    // states
    public enum GameState { RUNNING, WIN };
    public GameState gameState;
    public int level;

    // time
    private float elapsedTime;
    private float winTime;

    // HUD
    public Hud hud;

    public PlayScreen(AmazeGame game, String playerType, int level) {
        this.game = game;
        this.level = level;
        this.playerType = playerType;

        gameState = GameState.RUNNING;

        // camera and viewport (game world is set as square to reserve space for the HUDs at the sides)
        camera = new OrthographicCamera();
        viewport = new FitViewport(AmazeGame.VIEW_WIDTH / 4, AmazeGame.VIEW_HEIGHT / 4, camera);

        // Hud
        hud = new Hud(game.batch);

        // map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/level" + level + ".tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);

        // physics
        world = new World(new Vector2(0, 0), true);

        collisionListener = new CollisionListener(this);

        debugRenderer = new Box2DDebugRenderer(
                true, /* draw bodies */
                false, /* don't draw joints */
                false, /* don't draw aabbs */
                true, /* draw inactive bodies */
                false, /* don't draw velocities */
                true /* draw contacts */);

        // create player
        Vector2 playerSpawnLocation = MapPhysicsBuilder.getSpawnLocation(playerType + "_location", map).get(0);
        player = new Player(this, playerSpawnLocation.x, playerSpawnLocation.y);

        // create friend
        String friendType = playerType.equals("playerA") ? new String("playerB") : new String("playerA");
        Vector2 friendSpawnLocation = MapPhysicsBuilder.getSpawnLocation(friendType + "_location", map).get(0);
        friend = new Friend(this, friendSpawnLocation.x, friendSpawnLocation.y);

        // create monster
        Vector2 monsterSpawnLocation = MapPhysicsBuilder.getSpawnLocation("monster_location", map).get(0);
        monster = new Monster(this, monsterSpawnLocation.x, monsterSpawnLocation.y);

        // create items
        Vector2 healthSpawnLocation = MapPhysicsBuilder.getSpawnLocation("health_location", map).get(0);
        Vector2 laserSpawnLocation = MapPhysicsBuilder.getSpawnLocation("laser_location", map).get(0);
        Vector2 shieldSpawnLocation = MapPhysicsBuilder.getSpawnLocation("shield_location", map).get(0);

        healthPotion = new Item(this, Item.Type.HEALTH_POTION, healthSpawnLocation.x, healthSpawnLocation.y);
        laserGun = new Item(this, Item.Type.LASER_GUN, laserSpawnLocation.x, laserSpawnLocation.y);
        shield = new Item(this, Item.Type.SHIELD, shieldSpawnLocation.x, shieldSpawnLocation.y);

        // make walls
        Array<Body> bodies = MapPhysicsBuilder.buildShapes("wall", map, 1, world);

        // for networking
        game.networkClient.startMultiplayerGame();
    }

    public void openDoor() {
        map.getLayers().get("door").setVisible(false);
        map.getLayers().get("Tile Layer 3").setVisible(true);
    }

    public boolean checkWinState() {
        //to check if p1 and p2 are in the area of the door
        if((player.x > leftBound && player.x < rightBound) &&
                (friend.x > leftBound && friend.x < rightBound)){
            if((player.y > topBound && player.y < bottomBound) &&
                    (friend.y > topBound && friend.y < bottomBound)){
                Gdx.app.log("PlayScreen", "player 1 is at door: " + player.x  + " " +player.y );
                Gdx.app.log("PlayScreen", "player 2 is at door: " + friend.x  + " " +friend.y );
                Gdx.app.log("PlayScreen", "PLAYERS HAVE COMPLETED LEVEL!");
                return true;
            }
        }
        return false;
    }

    public void update(float delta) {

        elapsedTime += delta;

        switch (gameState) {
        case RUNNING:
            if (checkWinState()) {
                Gdx.app.log("PlayScreen", "Plays Winning music ~~~");
                openDoor();
                gameState = GameState.WIN;
                winTime = elapsedTime;
            }
            break;
        case WIN:
            if (level == game.MAX_LEVEL) return;

            // pause for about 2 seconds before to transit to next level
            if ((elapsedTime - winTime) > 2) {
                dispose();
                game.setScreen(new PlayScreen(game, playerType, level + 1));
            }
            return;
        }

        // get GameData from remote client
        GameData gameData = game.networkClient.getGameData();
        if (gameData != null) {
            if (gameData.msgType == GameData.MessageType.POSTGAME) {
                Gdx.app.log("PlayScreen", "Remote client disconnected.");
            } else {
                friend.update(delta, gameData);
            }
        }

        player.update(delta);
        monster.update(delta);

        // send GameData from remote client
        GameData dataToSend = new GameData();
        dataToSend.msgType = GameData.MessageType.INGAME;
        dataToSend.x = player.x;
        dataToSend.y = player.y;
        game.networkClient.sendGameData(dataToSend);

        //update items
        healthPotion.update(delta);
        laserGun.update(delta);
        shield.update(delta);

        // let camera follow player
        camera.position.x = player.x;
        camera.position.y = player.y;

        world.step(1 / 60f, 6, 2);

        camera.update();
        mapRenderer.setView(camera);
    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        viewport.apply();
        mapRenderer.render();
        //debugRenderer.render(world, viewport.getCamera().combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        friend.draw(game.batch);
        monster.draw(game.batch);

        //draw items
        healthPotion.draw(game.batch);
        laserGun.draw(game.batch);
        shield.draw(game.batch);

        game.batch.end();


        // draw HUD on top of everything else
        hud.stage.getViewport().apply();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.act(Gdx.graphics.getDeltaTime());
        hud.stage.draw();
    }

    @Override
    public void dispose() {
        map.dispose();
        hud.dispose();
        debugRenderer.dispose();
        world.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.stage.getViewport().update(width, height);
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
