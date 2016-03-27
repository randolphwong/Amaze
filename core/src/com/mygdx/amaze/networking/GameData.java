package com.mygdx.amaze.networking;

import java.io.Serializable;
import java.net.InetAddress;

public class GameData implements Serializable {

    private static final long serialVersionUID = 3L;
    
    public enum MessageType { PREGAME, INGAME, POSTGAME};
    public MessageType msgType;
    public String player;

    public long timeStamp;
    public float x;
    public float y;
    public String message;

    public InetAddress ipAddress;
    public int port;
}
