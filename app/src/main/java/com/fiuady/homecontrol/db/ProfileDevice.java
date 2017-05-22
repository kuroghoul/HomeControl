package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 21/05/2017.
 */

public class ProfileDevice {
    private int id;
    private int device_id;
    private boolean status1;
    private boolean status2;
    private int pwm1;
    private int pwm2;
    private int pwm3;

    public ProfileDevice(int id, int device_id, boolean status1, boolean status2, int pwm1, int pwm2, int pwm3) {
        this.id = id;
        this.device_id = device_id;
        this.status1 = status1;
        this.status2 = status2;
        this.pwm1 = pwm1;
        this.pwm2 = pwm2;
        this.pwm3 = pwm3;
    }

    public int getDevice_id() {
        return device_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getStatus1() {
        return status1;
    }

    public void setStatus1(boolean status1) {
        this.status1 = status1;
    }

    public boolean getStatus2() {
        return status2;
    }

    public void setStatus2(boolean status2) {
        this.status2 = status2;
    }

    public int getPwm1() {
        return pwm1;
    }

    public void setPwm1(int pwm1) {
        this.pwm1 = pwm1;
    }

    public int getPwm2() {
        return pwm2;
    }

    public void setPwm2(int pwm2) {
        this.pwm2 = pwm2;
    }

    public int getPwm3() {
        return pwm3;
    }

    public void setPwm3(int pwm3) {
        this.pwm3 = pwm3;
    }
}
