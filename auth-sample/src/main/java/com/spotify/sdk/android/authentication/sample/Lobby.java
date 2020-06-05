package com.spotify.sdk.android.authentication.sample;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Lobby implements Comparable< Lobby >, Serializable {
    private String id;
    private String hostID;
    private String name;
    private String genre;
    private String mood;
    private boolean publicType; //if it is public lobby or not (i called him "publicType" cause public is reached as public java token
    private int partecipantNumber;
    //there should be more attributes(?), but i don't exactly know wiches (maybe image of the lobby? the owner of the lobby?); so for now i don't touch them
    private String defaultMusicID;
    private String currentMusicID;
    private String nextMusicID;
    private String momentOfRefresh;
    private long musicDuration;

    //getters and setters


    public long getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(long musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getLobbyID() {
        return id;
    }

    public void setLobbyID(String id) {
        this.id = id;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public boolean getPublicType() {
        return publicType;
    }

    public void setPublicType(boolean publicType) {
        this.publicType = publicType;
    }

    public int getPartecipantNumber() {
        return partecipantNumber;
    }

    public void setPartecipantNumber(int partecipantNumber) {
        this.partecipantNumber = partecipantNumber;
    }

    public String getDefaultMusicID() {
        return defaultMusicID;
    }

    public void setDefaultMusicID(String defaultMusicID) {
        this.defaultMusicID = defaultMusicID;
    }

    public String getCurrentMusicID() {
        return currentMusicID;
    }

    public void setCurrentMusicID(String currentMusicID) {
        this.currentMusicID = currentMusicID;
    }

    @Override
    public int compareTo(Lobby lobby) {
        Integer i1 = this.getPartecipantNumber();
        Integer i2 = lobby.getPartecipantNumber();
        return i2.compareTo(i1);
    }

    public String getMomentOfRefresh() {
        return momentOfRefresh;
    }

    public void setMomentOfRefresh(String momentOfRefresh) {
        this.momentOfRefresh = momentOfRefresh;
    }

    public String getNextMusicID() {
        return nextMusicID;
    }

    public void setNextMusicID(String nextMusicID) {
        this.nextMusicID = nextMusicID;
    }
}
