package com.spotify.sdk.android.authentication.sample.ws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentlyPlayingContext {

    private Device device;
    private String repeat_state;
    private boolean shuffle_state;
    private Context context;
    private long timestamp;
    private int progress_ms;
    private boolean is_playing;
    private Track item;
    private String currently_playing_type;
    private Action actions;


    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getRepeat_state() {
        return repeat_state;
    }

    public void setRepeat_state(String repeat_state) {
        this.repeat_state = repeat_state;
    }

    public boolean isShuffle_state() {
        return shuffle_state;
    }

    public void setShuffle_state(boolean shuffle_state) {
        this.shuffle_state = shuffle_state;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getProgress_ms() {
        return progress_ms;
    }

    public void setProgress_ms(int progress_ms) {
        this.progress_ms = progress_ms;
    }

    public boolean isIs_playing() {
        return is_playing;
    }

    public void setIs_playing(boolean is_playing) {
        this.is_playing = is_playing;
    }

    public Track getItem() {
        return item;
    }

    public void setItem(Track item) {
        this.item = item;
    }

    public String getCurrently_playing_type() {
        return currently_playing_type;
    }

    public void setCurrently_playing_type(String currently_playing_type) {
        this.currently_playing_type = currently_playing_type;
    }

    public Action getActions() {
        return actions;
    }

    public void setActions(Action actions) {
        this.actions = actions;
    }



}
