package com.mygdx.amaze.networking;

import java.io.Serializable;
import java.net.InetAddress;

public class GameData implements Serializable {

    private static final long serialVersionUID = 3L;
    
    // general
    public enum MessageType { PREGAME, INGAME, POSTGAME};
    public MessageType msgType;
    public long timeStamp;

    // player
    public String player;
    public float x;
    public float y;
    public String message;

    // item 
    // TODO: rework all this hardcoding
    public boolean potionDestroyed;
    public boolean laserDestroyed;
    public boolean shieldDestroyed;

    // for server
    public InetAddress ipAddress;
    public int port;
}
