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
    private GameData gameData;
    private static int requestIdTracker;
    private long previousServerTimeStamp;

    private int playerShotsDone;

    public NetworkData(AmazeClient networkClient) {
        this.networkClient = networkClient;
        gameData = new GameData();
        requestIdTracker = 0;
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

    public void createDummyData() {
        gameData = new GameData();
    }

    public void resetGameData() {
        gameData.playerStatus = 0;
        gameData.monsterChasing = 0;
        gameData.itemTaken = 0;
    }

    public void initialiseLevel(Array<Item> items, Array<Monster> monsters, Player player, Friend friend) {
        if (gameData == null) gameData = new GameData();

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

    public void getInitialisationData() {
        setMessageType(Const.GET_INITIALISE);
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
        if (monster.isChasing()) {
            gameData.monsterChasing |= 1 << monsterIndex;
        }
    }

    public void setItemData(Item item) {
        int itemIndex = item.type.getValue();
        if (gameData.itemPosition == null) {
            gameData.itemPosition = new Coord[Const.MAX_ITEM];
        }
        gameData.itemPosition[itemIndex] = new Coord((short) item.posX, (short) item.posY);
        gameData.itemTaken |= item.isDestroyed() ? 1 << itemIndex : 0;
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

    public int requestMonsterChase(Monster monster) {
        gameData.monsterChasing |= 1 << (monster.getId() - 1);
        makeRequest();
        gameData.requestType = Const.MONSTER_CHASE_REQUEST;
        return gameData.requestId;
    }

    public int requestMonsterStopChase(Monster monster) {
        gameData.monsterChasing |= 1 << (monster.getId() - 1);
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
