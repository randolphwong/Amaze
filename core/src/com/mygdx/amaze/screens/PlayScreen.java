package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.amaze.AmazeGame;
import com.mygdx.amaze.collision.CollisionListener;
import com.mygdx.amaze.components.Earthquake;
import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.networking.GameData;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.screens.SplashScreen;
import com.mygdx.amaze.utilities.Coord;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.utilities.MapPhysicsBuilder;

/**
 * Created by Randolph on 12/3/2016.
 */
public class PlayScreen implements Screen {

    private AmazeGame game;

    // players
    public Player player;
    public Friend friend;
    public byte playerType;
    public static final String[] playerTypeString = {"playerA", "playerB"};
    public static final String[] friendTypeString = {"playerB", "playerA"};

    private Array<Monster> monsters;

    //Items
    public Item healthPotion;
    public Item laserGun;
    public Item shield;

    // camera and viewport
    public OrthographicCamera camera;
    public Viewport viewport;

    // tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // box2d
    private Box2DDebugRenderer debugRenderer;
    public World world;
    private CollisionListener collisionListener;

    private Rectangle level1DoorRect;
    private Rectangle level2DoorRect;

    // states
    public enum GameState { RUNNING, WIN, TIME_UP };
    public GameState gameState;
    public int level;

    // time
    private float elapsedTime;
    private float winTime;
    private float playTime;

    // HUD
    public Hud hud;

    // networking
    private NetworkData networkData;

    public PlayScreen(AmazeGame game, byte playerType, int level) {
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
        Vector2 playerSpawnLocation = MapPhysicsBuilder.getSpawnLocation(playerTypeString[playerType - 1] + "_location", map).get(0);
        player = new Player(this, playerSpawnLocation.x, playerSpawnLocation.y);

        // create friend
        Vector2 friendSpawnLocation = MapPhysicsBuilder.getSpawnLocation(friendTypeString[playerType - 1] + "_location", map).get(0);
        friend = new Friend(this, friendSpawnLocation.x, friendSpawnLocation.y);

        // create monster
        Monster.resetIdTracker(); // need this to prevent crash since ID tracker is static
        monsters = new Array<Monster>();
        Array<Vector2> monsterSpawnLocations = MapPhysicsBuilder.getSpawnLocation("monster_location", map);
        for (Vector2 monsterSpawnLocation : monsterSpawnLocations) {
            monsters.add(new Monster(this, monsterSpawnLocation));
        }

        // create items
        Vector2 healthSpawnLocation = MapPhysicsBuilder.getSpawnLocation("health_location", map).get(0);
        Vector2 laserSpawnLocation = MapPhysicsBuilder.getSpawnLocation("laser_location", map).get(0);
        Vector2 shieldSpawnLocation = MapPhysicsBuilder.getSpawnLocation("shield_location", map).get(0);

        healthPotion = new Item(this, Item.Type.HEALTH_POTION, healthSpawnLocation.x, healthSpawnLocation.y);
        laserGun = new Item(this, Item.Type.LASER_GUN, laserSpawnLocation.x, laserSpawnLocation.y);
        shield = new Item(this, Item.Type.SHIELD, shieldSpawnLocation.x, shieldSpawnLocation.y);

        // make walls
        Array<Body> bodies = MapPhysicsBuilder.buildShapes("wall", map, CollisionListener.WALL_BIT, world);

        // make monster boundaries
        Array<Body> monster_boundaries = MapPhysicsBuilder.buildShapes("monster_boundary", map, CollisionListener.MONSTER_BOUNDARY_BIT, world);

        // for networking
        game.networkClient.startMultiplayerGame();
        networkData = new NetworkData(game.networkClient);

        // door for level1/2
        level1DoorRect = new Rectangle(304, 1600 - 128, 192, 128);
        level2DoorRect = new Rectangle(144, 3200 - 128, 192, 128);
    }

    public void openDoor() {
        map.getLayers().get("door").setVisible(false);
        map.getLayers().get("Tile Layer 3").setVisible(true);
    }

    public boolean checkWinState() {
        //to check if p1 and p2 are in the area of the door
        switch (level) {
        case 1:
            if (level1DoorRect.contains(player.x, player.y) && 
                level1DoorRect.contains(friend.x, friend.y)) {
                return true;
            }
            break;
        case 2:
            if (level2DoorRect.contains(player.x, player.y) && 
                level2DoorRect.contains(friend.x, friend.y)) {
                return true;
            }
            break;
        }
        return false;
    }

    public void update(float delta) {
        elapsedTime += delta;
//        System.out.println(elapsedTime);

        switch (gameState) {
            case RUNNING:
                if (checkWinState()) {
                    Gdx.app.log("PlayScreen", "Plays Winning music ~~~");
                    openDoor();
                    gameState = GameState.WIN;
                    winTime = elapsedTime;
                }
                else if(hud.timer == 200){
                    map.getLayers().get("Tile Layer 4").setVisible(true);
                }
                else if(hud.timer == 100){
                    map.getLayers().get("Tile Layer 5").setVisible(true);
                }
                else if(hud.isTimeUp()){
                    Gdx.app.log("TimeUp", "Dun Dun Dun. Game over!");
                    gameState = GameState.TIME_UP;
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

            case TIME_UP:
                dispose();
                // TODO this is just a placeholder to prevent exception
                game.setScreen(new SplashScreen(game));
                return;
        }

        // get RawNetworkData from remote client
        networkData.getFromServer();
        if (networkData.isAvailable()) {
            if (networkData.messageType() == Const.POSTGAME) {
                Gdx.app.log("PlayScreen", "Remote client disconnected.");
            } else {
                friend.update(delta, networkData);
                for (Monster monster : monsters)
                    monster.update(delta, networkData);
            }
        } else {
            networkData.createDummyData();
        }

        player.update(delta);
        hud.update(delta);

        //update items
        healthPotion.update(delta, networkData);
        laserGun.update(delta, networkData);
        shield.update(delta, networkData);

        // send GameData from remote client
        networkData.resetGameData();
        networkData.setMessageType(Const.INGAME);
        // player
        networkData.setPlayerData(player);
        // monster
        for (Monster monster : monsters) {
            networkData.setMonsterData(monster);
        }
        // items
        networkData.setItemData(healthPotion);
        networkData.setItemData(laserGun);
        networkData.setItemData(shield);
        networkData.sendToServer();


        if(hud.earthquake.time>0){
            hud.earthquake.tick(delta, this, player);
        }
        else{
            // let camera follow player
            camera.position.x = player.x;
            camera.position.y = player.y;
        }
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

        // don't render if game ended (or else we will get seg fault!)
        if (gameState != GameState.RUNNING) return;

        viewport.apply();
        mapRenderer.render();
        debugRenderer.render(world, viewport.getCamera().combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // draw monsters
        for (Monster monster : monsters)
            monster.draw(game.batch);

        //draw items
        healthPotion.draw(game.batch);
        laserGun.draw(game.batch);
        shield.draw(game.batch);

        // draw players
        friend.draw(game.batch);
        player.draw(game.batch);

        game.batch.end();


        // draw HUD on top of everything else
        hud.stage.getViewport().apply();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.act(Gdx.graphics.getDeltaTime());
        hud.stage.draw();
    }

    @Override
    public void dispose() {
        for (Monster monster : monsters)
            monster.dispose();
        player.dispose();
        friend.dispose();
        healthPotion.dispose();
        laserGun.dispose();
        shield.dispose();

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
