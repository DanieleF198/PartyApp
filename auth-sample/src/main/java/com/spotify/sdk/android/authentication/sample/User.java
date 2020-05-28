package com.spotify.sdk.android.authentication.sample;

public class User {

    private String id;
    private String username;
    private String password;
    private boolean remember;

    public User(String username, String password,  boolean remember) {
        this.username = username;
        this.password = password;
        this.remember = remember;
    }

    public User(boolean remember){
        this.remember = false;
    }

    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRemember() {
        return remember;
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

    public boolean getRemember() {
        return remember;
    }

    public void setRemember(boolean remember) { this.remember = remember; }

}
