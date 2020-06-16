package com.spotify.sdk.android.authentication.sample.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist {
    private Paging tracks;

    public Paging getTracks() {
        return tracks;
    }

    public void setTracks(Paging tracks) {
        this.tracks = tracks;
    }
}
