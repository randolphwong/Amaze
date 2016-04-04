package com.mygdx.amaze.utilities;

import java.io.Serializable;

public class Coord implements Serializable {

    private static final long serialVersionUID = 1264333L;
    public short x;
    public short y;

    public Coord(short x, short y) {
        this.x = x;
        this.y = y;
    }
}
