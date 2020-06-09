package com.spotify.sdk.android.authentication.sample.ws.service;

import com.spotify.sdk.android.authentication.sample.ws.model.CurrentlyPlayingContext;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface PlayerService {

    @GET("me/player")
    Call<CurrentlyPlayingContext> getInfoCurrentUserPlayback(@Header("Authorization") String accessToken);
}
