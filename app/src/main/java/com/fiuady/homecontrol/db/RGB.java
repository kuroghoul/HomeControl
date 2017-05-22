package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 21/05/2017.
 */

public class RGB {
    private int id;
    private int status_onOff;
    private int status_ambiente;
    private int R;
    private int G;
    private int B;

    public RGB(int id, int status_onOff, int status_ambiente, int r, int g, int b) {
        this.id = id;
        this.status_onOff = status_onOff;
        this.status_ambiente = status_ambiente;
        R = r;
        G = g;
        B = b;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus_onOff() {
        return status_onOff;
    }

    public void setStatus_onOff(int status_onOff) {
        this.status_onOff = status_onOff;
    }

    public int getStatus_ambiente() {
        return status_ambiente;
    }

    public void setStatus_ambiente(int status_ambiente) {
        this.status_ambiente = status_ambiente;
    }

    public int getR() {
        return R;
    }

    public void setR(int r) {
        R = r;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }
}
