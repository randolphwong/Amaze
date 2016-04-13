package com.mygdx.amaze.utilities;

public enum ItemType {

    HEALTH_POTION(0), 
    LASER_GUN(1),
    SHIELD(2);

    private int value = 0;

    static {
        HEALTH_POTION.value = 0;
        LASER_GUN.value = 1;
        SHIELD.value = 2;
    }

    ItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ItemType valueOf(int value) {
        for (ItemType type : ItemType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

}
