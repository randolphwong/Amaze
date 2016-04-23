package com.mygdx.amaze.networking;

import com.badlogic.gdx.utils.Array;

import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.entities.Friend;
import com.mygdx.amaze.entities.Player.FaceState;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.utilities.Coord;
import com.mygdx.amaze.utilities.ItemType;

public class NetworkData {

    private AmazeClient networkClient;
    private byte clientType;
    private GameData gameData;
    private static int requestIdTracker;
    private long previousServerTimeStamp;

    private int playerShotsDone;

    public NetworkData(byte clientType, AmazeClient networkClient) {
        this.networkClient = networkClient;
        this.clientType = clientType;
        gameData = new GameData();
        requestIdTracker = 0;
    }

    public GameData weaklyCloneGameData(GameData gameData) {
        GameData cloned = new GameData();

        cloned.msgType = gameData.msgType;
        cloned.playerStatus = gameData.playerStatus;
        cloned.monsterChasing = gameData.monsterChasing;
        cloned.itemTaken = gameData.itemTaken;
        cloned.clientTimeStamp = gameData.clientTimeStamp;
        cloned.ServerTimeStamp = gameData.ServerTimeStamp;
        cloned.requestId = gameData.requestId;
        cloned.requestType = gameData.requestType;
        cloned.requestOutcome = gameData.requestOutcome;

        return cloned;
    }

    public GameData cloneGameData(GameData gameData) {
        GameData cloned = new GameData();

        if (gameData.playerPosition != null) {
            cloned.playerPosition = new Coord(gameData.playerPosition.x, gameData.playerPosition.y);
        }
        if (gameData.friendPosition != null) {
            cloned.friendPosition = new Coord(gameData.friendPosition.x, gameData.friendPosition.y);
        }
        if (gameData.monsterPosition != null) {
            cloned.monsterPosition = new Coord[gameData.monsterPosition.length];
            for (int i = 0; i < gameData.monsterPosition.length; i++) {
                if (gameData.monsterPosition[i] != null) {
                    cloned.monsterPosition[i] = new Coord(gameData.monsterPosition[i].x, gameData.monsterPosition[i].y);
                }
            }
        }
        if (gameData.itemPosition != null) {
            cloned.itemPosition = new Coord[gameData.itemPosition.length];
            for (int i = 0; i < gameData.itemPosition.length; i++) {
                if (gameData.itemPosition[i] != null) {
                    cloned.itemPosition[i] = new Coord(gameData.itemPosition[i].x, gameData.itemPosition[i].y);
                }
            }
        }

        cloned.msgType = gameData.msgType;
        cloned.playerStatus = gameData.playerStatus;
        cloned.monsterChasing = gameData.monsterChasing;
        cloned.itemTaken = gameData.itemTaken;
        cloned.ipAddress = gameData.ipAddress;
        cloned.port = gameData.port;
        cloned.clientTimeStamp = gameData.clientTimeStamp;
        cloned.ServerTimeStamp = gameData.ServerTimeStamp;
        cloned.requestId = gameData.requestId;
        cloned.requestType = gameData.requestType;
        cloned.requestOutcome = gameData.requestOutcome;

        return cloned;
    }

    public boolean isAvailable() {
        return gameData != null;
    }

    public void getFromServer() {
        gameData = networkClient.getGameData();
        if (gameData == null) return;

        // ignore old packets
        if (gameData.ServerTimeStamp <= previousServerTimeStamp) {
            gameData = null;
        } else {
            previousServerTimeStamp = gameData.ServerTimeStamp;
        }
    }

    public void sendToServer() {
        networkClient.sendGameData(gameData);
    }

    public long timeStamp() {
        return gameData.ServerTimeStamp;
    }

    public void createDummyData() {
        gameData = new GameData();
    }

    public void resetGameData() {
        gameData.playerStatus = 0;
        gameData.monsterChasing = 0;
        gameData.itemTaken = 0;
    }

    public void initialiseLevel(int level, Array<Item> items, Array<Monster> monsters, Player player, Friend friend) {
        if (gameData == null) gameData = new GameData();

        gameData.level = (byte) level;

        setMessageType(Const.INITIALISE);
        for (Item item : items) {
            setItemData(item);
        }
        for (Monster monster : monsters) {
            setMonsterData(monster);
        }
        setPlayerData(player);
        gameData.friendPosition = new Coord((short)friend.x, (short)friend.y);
        sendToServer();
    }

    public void getInitialisationData(int level) {
        setMessageType(Const.GET_INITIALISE);
        gameData.level = (byte) level;
        sendToServer();

        while (true) {
            getFromServer();
            if (gameData != null) {
                if (gameData.msgType == Const.INITIALISE) return;
            }
        }
    }

    public void setMessageType(byte messageType) {
        gameData.msgType = messageType;
    }

    public void setPlayerData(Player player) {
        gameData.playerPosition = new Coord((short)player.x, (short)player.y);
        switch(player.faceState) {
        case UP: gameData.playerStatus |= Const.FACE_UP; break;
        case DOWN: gameData.playerStatus |= Const.FACE_DOWN; break;
        case LEFT: gameData.playerStatus |= Const.FACE_LEFT; break;
        case RIGHT: gameData.playerStatus |= Const.FACE_RIGHT; break;
        }
        gameData.playerStatus |= player.attacked ? Const.ATTACKED : 0;
        gameData.playerStatus |= player.shielded ? Const.SHIELDED : 0;
        gameData.playerStatus |= player.dead ? Const.DEAD : 0;
        if (player.shotsDone > playerShotsDone) {
            playerShotsDone = player.shotsDone;
            gameData.playerStatus |= Const.SHOOTING;
        }
    }

    public void setMonsterData(Monster monster) {
        int monsterIndex = monster.getId() - 1;
        if (gameData.monsterPosition == null) {
            gameData.monsterPosition = new Coord[Const.MAX_MONSTER];
        }
        gameData.monsterPosition[monsterIndex] = new Coord((short) monster.position.x, (short) monster.position.y);
    }

    public void setItemData(Item item) {
        int itemIndex = item.type.getValue();
        if (gameData.itemPosition == null) {
            gameData.itemPosition = new Coord[Const.MAX_ITEM];
        }
        gameData.itemPosition[itemIndex] = new Coord((short) item.posX, (short) item.posY);
    }

    public byte messageType() {
        return gameData.msgType;
    }

    public byte clientType() {
        return gameData.clientType;
    }

    public Coord playerPosition() {
        return gameData.playerPosition;
    }

    public Coord friendPosition() {
        return gameData.friendPosition;
    }

    public FaceState playerFaceState() {
        FaceState faceState = null;
        byte faceStateBits = Const.FACE_UP | Const.FACE_DOWN | Const.FACE_LEFT | Const.FACE_RIGHT;
        switch (gameData.playerStatus & faceStateBits) {
        case Const.FACE_UP: faceState = FaceState.UP; break;
        case Const.FACE_DOWN: faceState = FaceState.DOWN; break;
        case Const.FACE_LEFT: faceState = FaceState.LEFT; break;
        case Const.FACE_RIGHT: faceState = FaceState.RIGHT; break;
        }
        return faceState;
    }

    public boolean isPlayerAttacked() {
        return (gameData.playerStatus & Const.ATTACKED) != 0;
    }

    public boolean isPlayerShielded() {
        return (gameData.playerStatus & Const.SHIELDED) != 0;
    }

    public boolean isPlayerShooting() {
        return (gameData.playerStatus & Const.SHOOTING) != 0;
    }

    public boolean isPlayerDead() {
        return (gameData.playerStatus & Const.DEAD) != 0;
    }

    public Coord monsterPosition(Monster monster) {
        if (gameData.monsterPosition == null) return null;
        return gameData.monsterPosition[monster.getId() - 1];
    }

    public boolean isMonsterChasing(Monster monster) {
        gameData.monsterChasing |= gameData.monsterChasing >> 8;
        return (gameData.monsterChasing & (1 << (monster.getId() - 1))) != 0;
    }

    public Coord itemPosition(Item item) {
        if (gameData.itemPosition == null) return null;
        return gameData.itemPosition[item.type.getValue()];
    }

    public Coord itemPosition(ItemType type) {
        if (gameData.itemPosition == null) return null;
        return gameData.itemPosition[type.getValue()];
    }

    public boolean isItemTaken(Item item) {
        return (gameData.itemTaken & (1 << item.type.getValue())) != 0;
    }

    public int getRequestId() {
        return gameData.requestId;
    }

    public boolean getRequestOutcome() {
        return gameData.requestOutcome;
    }

    public int requestItem(Item item) {
        gameData.itemTaken |= 1 << item.type.getValue();
        makeRequest();
        gameData.requestType = Const.ITEM_REQUEST;
        return gameData.requestId;
    }

    public int requestItemRespawn(Item item) {
        gameData.itemTaken |= 1 << item.type.getValue();
        makeRequest();
        gameData.requestType = Const.ITEM_RESPAWN_REQUEST;
        return gameData.requestId;
    }

    public int requestMonsterChase(Monster monster) {
        short request = 0;
        request = (short) (1 << (monster.getId() - 1));
        if (clientType == Const.MASTER_CLIENT) {
            request <<= 8;
        }
        gameData.monsterChasing = request;
        makeRequest();
        gameData.requestType = Const.MONSTER_CHASE_REQUEST;
        return gameData.requestId;
    }

    public int requestMonsterStopChase(Monster monster) {
        short request = 0;
        request = (short) (1 << (monster.getId() - 1));
        if (clientType == Const.MASTER_CLIENT) {
            request <<= 8;
        }
        gameData.monsterChasing = request;
        makeRequest();
        gameData.requestType = Const.MONSTER_STOP_CHASE_REQUEST;
        return gameData.requestId;
    }

    private void makeRequest() {
        if (gameData.msgType != Const.REQUEST) {
            gameData.msgType = Const.REQUEST;
            gameData.requestId = ++requestIdTracker;
        }
    }
}
