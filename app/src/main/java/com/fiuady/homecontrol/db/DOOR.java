package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 16/05/2017.
 */

public class DOOR {
    boolean opened;

    public DOOR(boolean opened) {
        this.opened = opened;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
