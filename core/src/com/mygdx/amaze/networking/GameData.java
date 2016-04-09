package com.mygdx.amaze.networking;

import java.io.Serializable;
import java.net.InetAddress;

import com.mygdx.amaze.utilities.Coord;

public class GameData implements Serializable {

    private static final long serialVersionUID = 1264332L;
    
    // general
    public byte msgType;

    // player
    public Coord playerPosition;
    public byte playerStatus;

    // monster
    public byte monsterChasing; // bit manipulation
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
    public long acknowledgement;
    public int requestId;
    public byte requestType;
    public boolean requestOutcome;
}

