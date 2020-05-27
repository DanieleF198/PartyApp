package com.spotify.sdk.android.authentication.sample;

public class Lobby {
    private String lobbyID;
    private String name;
    private String genre;
    private String mood;
    private boolean publicType; //if it is public lobby or not (i called him "publicType" cause public is reached as public java token
    private int partecipantNumber;
    //there should be more attributes(?), but i don't exactly know wiches (maybe image of the lobby? the owner of the lobby?); so for now i don't touch them

    //getters and setters
    public String getId() {
        return lobbyID;
    }

    public void setId(String lobbyID) {
        this.lobbyID = lobbyID;
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

    public boolean getPublicType() { return publicType; }

    public void setPublicType(boolean publicType) {
        this.publicType = publicType;
    }

    public int getPartecipantNumber() {
        return partecipantNumber;
    }

    public void setPartecipantNumber(int partecipantNumber) { this.partecipantNumber = partecipantNumber; }
}
