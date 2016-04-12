package com.mygdx.amaze.networking;

import com.badlogic.gdx.utils.Array;

import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.utilities.Coord;

public class NetworkData {

    private AmazeClient networkClient;
    private GameData gameData;
    private static int requestIdTracker;
    private long previousServerTimeStamp;

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

    public void initialiseLevel(Item i1, Item i2, Item i3, Array<Monster> monsters) {
        if (gameData == null) gameData = new GameData();

        setMessageType(Const.INITIALISE);
        setItemData(i1);
        setItemData(i2);
        setItemData(i3);
        for (Monster monster : monsters) {
            setMonsterData(monster);
        }
        sendToServer();
    }

    public void setMessageType(byte messageType) {
        gameData.msgType = messageType;
    }

    public void setPlayerData(Player player) {
        gameData.playerPosition = new Coord((short)player.x, (short)player.y);
        gameData.playerStatus |= player.attacked ? Const.ATTACKED : 0;
        gameData.playerStatus |= player.shielded ? Const.SHIELDED : 0;
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
        int itemIndex = item.type.ordinal();
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

    public boolean isPlayerAttacked() {
        return gameData.playerStatus == Const.ATTACKED;
    }

    public boolean isPlayerShielded() {
        return gameData.playerStatus == Const.SHIELDED;
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
        return gameData.itemPosition[item.type.ordinal()];
    }

    public boolean isItemTaken(Item item) {
        return (gameData.itemTaken & (1 << item.type.ordinal())) != 0;
    }

    public int getRequestId() {
        return gameData.requestId;
    }

    public boolean getRequestOutcome() {
        return gameData.requestOutcome;
    }

    public int requestItem(Item item) {
        gameData.itemTaken |= 1 << item.type.ordinal();
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
