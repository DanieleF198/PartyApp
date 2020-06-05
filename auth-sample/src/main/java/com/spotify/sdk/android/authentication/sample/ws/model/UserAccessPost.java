package com.spotify.sdk.android.authentication.sample.ws.model;

import com.google.gson.annotations.SerializedName;

public class UserAccessPost {


    @SerializedName("grant_type")
    private String grant_type;


    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

}
