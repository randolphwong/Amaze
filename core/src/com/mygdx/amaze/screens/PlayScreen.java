package com.mygdx.amaze.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.networking.NetworkData;
import com.mygdx.amaze.networking.RequestManager;
import com.mygdx.amaze.entities.Projectile;
import com.mygdx.amaze.scenes.Hud;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.utilities.MapPhysicsBuilder;
import com.mygdx.amaze.utilities.ItemType;

/**
 * Created by Randolph on 12/3/2016.
 */
public class PlayScreen implements Screen {

    private AmazeGame game;

    // constants for earthquake
    public static final int TIME_TILL_GROUND_CRACK = 150;
    public static final int TIME_TILL_GROUND_BREAK = 50;

    // players
    public Player player;
    public Friend friend;
    public byte clientType;
    public static final String[] playerTypeString = {"playerA", "playerB"};
    public static final String[] friendTypeString = {"playerB", "playerA"};
    public Array<Vector2> availablePlayerPositions;

    public Array<Projectile> projectiles;
    private Array<Monster> monsters;


    //Items
    public Array<Item> items;
    public Array<Vector2> availableItemPositions;

    // camera and viewport
    public OrthographicCamera camera;
    public Viewport viewport;

    // tiled map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // box2d
//    private Box2DDebugRenderer debugRenderer;
    public World world;
    private CollisionListener collisionListener;

    public static final Rectangle[] doorRect = new Rectangle[] {new Rectangle(320, 1600 - 144, 175, 144),
                                                                new Rectangle(144, 3200 - 128, 192, 128)};

    // states
    public enum GameState { RUNNING, WIN, SCREEN_CHANGE, TIME_UP };
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
    private RequestManager requestManager;
    private byte networkSendDelay = 1;


    //music
    private Music level_1 = Gdx.audio.newMusic(Gdx.files.internal("music/urgent.mp3"));
    private Music level_2 = Gdx.audio.newMusic(Gdx.files.internal("music/black_star.mp3"));

    public PlayScreen(AmazeGame game, byte clientType, int level) {
        this.game = game;
        this.level = level;
        this.clientType = clientType;
        items = new Array<Item>();

        gameState = GameState.RUNNING;

        // camera and viewport (game world is set as square to reserve space for the HUDs at the sides)
        camera = new OrthographicCamera();
        viewport = new FitViewport(AmazeGame.VIEW_WIDTH / 4, AmazeGame.VIEW_HEIGHT / 4, camera);

        // Hud
        hud = new Hud(game.batch, this);

        // map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/level" + level + ".tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);

        // physics
        world = new World(new Vector2(0, 0), true);

        collisionListener = new CollisionListener(this);

        /*
         *debugRenderer = new Box2DDebugRenderer(
         *        true, [> draw bodies <]
         *        false, [> don't draw joints <]
         *        false, [> don't draw aabbs <]
         *        true, [> draw inactive bodies <]
         *        false, [> don't draw velocities <]
         *        true [> draw contacts <]);
         */


        if (clientType == Const.MASTER_CLIENT) {
            // create player
            availablePlayerPositions = MapPhysicsBuilder.getSpawnLocation("obj_player", map);
            Vector2 playerSpawnLocation = availablePlayerPositions.random();
            availablePlayerPositions.removeValue(playerSpawnLocation, true);
            player = new Player(this, playerSpawnLocation.x, playerSpawnLocation.y);

            // create friend
            playerSpawnLocation = availablePlayerPositions.random();
            friend = new Friend(this, playerSpawnLocation.x, playerSpawnLocation.y);

            // create items
            availableItemPositions = MapPhysicsBuilder.getSpawnLocation("obj_item", map);
            Vector2 nextItemPosition;
            for (int i = 0; i < Const.MAX_ITEM; i++) {
                nextItemPosition = getRandomItemPosition();
                items.add(new Item(this, ItemType.valueOf(i), nextItemPosition.x, nextItemPosition.y));
            }
        }


        // create monster
        Monster.resetIdTracker(); // need this to prevent crash since ID tracker is static
        monsters = new Array<Monster>();
        Array<Vector2> monsterSpawnLocations = MapPhysicsBuilder.getSpawnLocation("monster_location", map);
        for (Vector2 monsterSpawnLocation : monsterSpawnLocations) {
            monsters.add(new Monster(this, monsterSpawnLocation));
        }

        // create projectiles
        projectiles = new Array<Projectile>();

        // make walls
        Array<Body> bodies = MapPhysicsBuilder.buildShapes("wall", map, CollisionListener.WALL_BIT, world);

        // make ground holes
        Array<Body> holes = MapPhysicsBuilder.buildShapes("obj_hole", map, CollisionListener.HOLE_BIT, world);

        // make monster boundaries
        Array<Body> monster_boundaries = MapPhysicsBuilder.buildShapes("monster_boundary", map, CollisionListener.MONSTER_BOUNDARY_BIT, world);

        // for networking
        game.networkClient.startMultiplayerGame();
        networkData = new NetworkData(clientType, game.networkClient);
        if (clientType == Const.MASTER_CLIENT) {
            networkData.initialiseLevel(items, monsters, player, friend);
        } else {
            // this will busy wait
            // TODO: maybe should timeout?
            networkData.getInitialisationData();
            player = new Player(this, networkData.friendPosition().x, networkData.friendPosition().y);
            friend = new Friend(this, networkData.playerPosition().x, networkData.playerPosition().y);

            for (int i = 0; i < Const.MAX_ITEM; i++) {
                ItemType type = ItemType.valueOf(i);
                items.add(new Item(this, type, networkData.itemPosition(type).x, networkData.itemPosition(type).y));
            }
        }
        requestManager = RequestManager.getInstance();
    }

    public Vector2 getRandomItemPosition() {
        Vector2 nextItemPosition = availableItemPositions.random();
        if (nextItemPosition != null) {
            availableItemPositions.removeValue(nextItemPosition, true);
        }
        return nextItemPosition;
    }

    public void addAvailableItemPosition(Vector2 position) {
        if (position != null) {
            availableItemPositions.add(position);
        }
    }

    public void openDoor() {
        map.getLayers().get("Tile Layer 3").setVisible(true);
    }

    public boolean checkWinState() {
        //to check if p1 and p2 are in the area of the door
        if (doorRect[level-1].contains(player.x, player.y) && 
            doorRect[level-1].contains(friend.x, friend.y)) {
            return true;
        }
        return false;
    }

    public void update(float delta) {
        elapsedTime += delta;
//        System.out.println(elapsedTime);

        switch (gameState) {
            case RUNNING:
                if(level == 1){
                    level_1.setLooping(true);
                    level_1.play();
                }
                else if(level == 2){
                    level_2.setLooping(true);
                    level_2.play();
                }
                if (checkWinState()) {
                    Gdx.app.log("PlayScreen", "Plays Winning music ~~~");
                    openDoor();
                    gameState = GameState.WIN;
                    winTime = elapsedTime;
                }
                else if(hud.timer == TIME_TILL_GROUND_CRACK){
                    map.getLayers().get("Tile Layer 4").setVisible(true);
                }
                else if(hud.timer == TIME_TILL_GROUND_BREAK){
                    player.makeCollidableWithHole();
                    map.getLayers().get("Tile Layer 5").setVisible(true);
                }
                else if(hud.isTimeUp()){
                    Gdx.app.log("TimeUp", "Dun Dun Dun. Game over!");
                    gameState = GameState.TIME_UP;
                }
                break;

            case WIN:
                // pause for about 1 seconds before to transit to next level
                if ((elapsedTime - winTime) > 2) {
                    gameState = GameState.SCREEN_CHANGE;
                }
                break;

            case SCREEN_CHANGE:
                dispose();
                if (level == game.MAX_LEVEL){
                    level_2.stop();
                    level_2.dispose();
                    game.setScreen(new WinScreen(game, this));
                } else {
                    level_1.stop();
                    level_1.dispose();
                    game.setScreen(new PlayScreen(game, clientType, level + 1));
                    level_2.setLooping(true);
                    level_2.play();
                }
                return;

            case TIME_UP:
                level_1.stop();
                level_1.dispose();
                dispose();
                if(elapsedTime -winTime >2){
                    // TODO this is just a placeholder to prevent exception
                    game.setScreen(new SplashScreen(game));
                }
                return;
        }

        // update physics world
        world.step(1 / 60f, 6, 2);

        // send requests to server if any
        requestManager.makeRequest(networkData);

        // get RawNetworkData from server
        networkData.getFromServer();
        if (networkData.isAvailable()) {
            switch (networkData.messageType()) {
            case Const.POSTGAME:
                Gdx.app.log("PlayScreen", "Remote client disconnected.");
                break;
            case Const.REQUEST:
                requestManager.resolve(networkData);
                break;
            }
        } else {
            networkData.createDummyData();
        }

        player.update(delta, friend);
        hud.update(delta);
        for(Projectile projectile : projectiles){
            projectile.update(delta);
        }

        if (networkData.messageType() != Const.REQUEST) {
            // update friend
            friend.update(delta, networkData);

            // update monsters
            for (Monster monster : monsters)
                monster.update(delta, networkData);

            //update items
            for (Item item : items)
                item.update(delta, networkData);
        }

        // send RawNetworkData to remote client
        if (networkSendDelay == 1) {
            networkData.resetGameData();
            networkData.setMessageType(Const.INGAME);
            networkData.setPlayerData(player);
            for (Monster monster : monsters) {
                networkData.setMonsterData(monster);
            }
            for (Item item : items) {
                networkData.setItemData(item);
            }
            networkData.sendToServer();
        }
        // send update only at every 3rd frame
        networkSendDelay <<= 1;
        if (networkSendDelay == 8) networkSendDelay = 1;


        if(hud.earthquake.time>0){
            hud.earthquake.tick(delta, this, player);
        }
        else{
            // let camera follow player
            camera.position.x = player.x;
            camera.position.y = player.y;
        }

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

        // don't render if game ended (or else we will get seg fault!)
/*
 *        if (gameState == GameState.RUNNING) {
 *            debugRenderer.render(world, viewport.getCamera().combined);
 *        }
 */

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // draw monsters
        for (Monster monster : monsters) {
            monster.draw(game.batch);
        }

        // draw projectiles
        for(Projectile projectile : projectiles){
            projectile.draw(game.batch);
        }

        //draw items
        for (Item item : items) {
            item.draw(game.batch);
        }

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
        for (Item item : items)
            item.dispose();
        for(Projectile p : projectiles){
            p.dispose();
        }
        map.dispose();
        hud.dispose();
        //debugRenderer.dispose();
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
