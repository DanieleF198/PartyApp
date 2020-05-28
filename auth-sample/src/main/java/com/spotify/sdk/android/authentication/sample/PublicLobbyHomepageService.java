package com.spotify.sdk.android.authentication.sample;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PublicLobbyHomepageService {
    @GET("lobbys")
    Call<List<Lobby>> getLobbys();
}
