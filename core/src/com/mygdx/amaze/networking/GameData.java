package com.mygdx.amaze.networking;

import java.io.Serializable;

import com.mygdx.amaze.utilities.Coord;

public class GameData implements Serializable {

    private static final long serialVersionUID = 1264332L;
    
    // general
    public byte msgType;
    public byte level;

    // player
    public Coord playerPosition;
    public byte playerStatus;

    // only for initialisation for remote client
    public Coord friendPosition;

    // monster
    public short monsterChasing; // bit manipulation
    public Coord[] monsterPosition;

    // item 
    public byte itemTaken; // bit manipulation
    public Coord[] itemPosition;

    // for networking
    public byte clientType;
    public String ipAddress;
    public int port;
    public long clientTimeStamp;
    public long ServerTimeStamp;
    public int requestId;
    public byte requestType;
    public boolean requestOutcome;
}
