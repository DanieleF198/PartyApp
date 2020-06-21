package com.spotify.sdk.android.authentication.sample.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {
    private String name;
    private Artist[] artists;
    private Image[] images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist[] getArtists() {
        return artists;
    }

    public void setArtists(Artist[] artists) {
        this.artists = artists;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }
}
