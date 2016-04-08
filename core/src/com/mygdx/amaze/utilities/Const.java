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

    // When game starts, this will determine whether the client is the main or
    // 'other' player
    public static final byte MAIN_PLAYER = 1;
    public static final byte OTHER_PLAYER = 2;

    // player status
    public static final byte ATTACKED = 1;
    public static final byte SHIELDED = 2;
    public static final byte DEAD = 4;

    public static final int MAX_MONSTER = 4;
    public static final int MAX_ITEM = 4;
}
