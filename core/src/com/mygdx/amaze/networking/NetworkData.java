package com.mygdx.amaze.networking;

import com.mygdx.amaze.entities.Item;
import com.mygdx.amaze.entities.Monster;
import com.mygdx.amaze.entities.Player;
import com.mygdx.amaze.utilities.Const;
import com.mygdx.amaze.utilities.Coord;

public class NetworkData {

    private AmazeClient networkClient;
    private GameData gameData;

    public NetworkData(AmazeClient networkClient) {
        this.networkClient = networkClient;
        gameData = new GameData();
    }

    public boolean isAvailable() {
        return gameData != null;
    }

    public void getFromServer() {
        gameData = networkClient.getGameData();
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
        gameData.monsterChasing |= 1 << monsterIndex;
    }

    public void setItemData(Item item) {
        gameData.itemTaken |= item.isDestroyed() ? 1 << item.type.ordinal() : 0;
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
        return gameData.monsterPosition[monster.getId() - 1];
    }

    public boolean isMonsterChasing(Monster monster) {
        return (gameData.monsterChasing & (1 << (monster.getId() - 1))) != 0;
    }

    public Coord itemPosition(Item item) {
        return gameData.monsterPosition[item.type.ordinal()];
    }

    public boolean isItemTaken(Item item) {
        return (gameData.itemTaken & (1 << item.type.ordinal())) != 0;
    }
}
