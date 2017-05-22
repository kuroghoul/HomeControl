package com.fiuady.homecontrol.db;

/**
 * Created by Kuro on 20/05/2017.
 */

public class UserProfile {
    private int id;
    private int userId;
    private String description;
    private boolean active;

    public UserProfile(int id, int userId, String description, boolean active) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
