package com.spotify.sdk.android.authentication.sample.ws.model;

public class UserRemember {

    private String id;
    private String username;
    private String password;
    private boolean remember;

    public UserRemember(String id, String username, String password, boolean remember){

        this.id = id;
        this.username = username;
        this.password = password;
        this.remember = remember;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
}
