package com.spotify.sdk.android.authentication.sample.ws.model;

public class PlayResumePlayback {

    private String[] uris;

    public long getPosition_ms() {
        return position_ms;
    }

    public void setPosition_ms(long position_ms) {
        this.position_ms = position_ms;
    }

    private long position_ms;

    public String[] getUris() {
        return uris;
    }

    public void setUris(String[] uris) {
        this.uris = uris;
    }
}
