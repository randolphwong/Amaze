package com.mygdx.amaze.utilities;

public class Const {

    /*
     * Constants for GameData
     */

    // PREGAME, INGAME and POSTGAME categorise the type of network message it
    // is. See the networking documentation for more information.
    public static final byte PREGAME = 1;
    public static final byte INGAME = 2;
    public static final byte POSTGAME = 3;
    public static final byte INITIALISE = 4;
    public static final byte GET_INITIALISE = 5;
    public static final byte REQUEST = 6;

    public static final byte MASTER_CLIENT = 1;
    public static final byte SLAVE_CLIENT = 2;

    // player status
    public static final byte ATTACKED    = 1 << 0;
    public static final byte SHIELDED    = 1 << 1;
    public static final byte SHOOTING    = 1 << 2;
    public static final byte DEAD        = 1 << 3;
    public static final byte FACE_LEFT   = 1 << 4;
    public static final byte FACE_RIGHT  = 1 << 5;
    public static final byte FACE_UP     = 1 << 6;
    public static final byte FACE_DOWN   = (byte) 128;

    // request types
    public static final byte ITEM_REQUEST = 1;
    public static final byte ITEM_RESPAWN_REQUEST = 2;
    public static final byte MONSTER_CHASE_REQUEST = 3;
    public static final byte MONSTER_STOP_CHASE_REQUEST = 4;

    public static final int MAX_MONSTER = 4;
    public static final int MAX_ITEM = 3;
}
