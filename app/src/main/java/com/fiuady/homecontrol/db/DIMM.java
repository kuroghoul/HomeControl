package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 16/05/2017.
 */

public class DIMM {

    private int pwm;
    private boolean enable;

    public DIMM(int pwm, boolean enable) {
        this.pwm = pwm;
        this.enable = enable;
    }

    public int getPwm() {
        return pwm;
    }

    public void setPwm(int pwm) {
        this.pwm = pwm;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
