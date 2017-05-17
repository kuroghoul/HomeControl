package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 16/05/2017.
 */

public class SW {
    boolean opened;
    boolean alarm;

    public SW(boolean opened, boolean alarm) {
        this.opened = opened;
        this.alarm = alarm;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }
}
