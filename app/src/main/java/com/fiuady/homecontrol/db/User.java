package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 20/05/2017.
 */

public class User {
    private int id;
    private String user;
    private String password;
    private String nip;

    public User(int id, String user, String password, String nip) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.nip = nip;
    }


    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }
}
