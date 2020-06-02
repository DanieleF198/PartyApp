package com.spotify.sdk.android.authentication.sample;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface PublicLobbyHomepageService {
    @GET("lobbys")
    Call<List<Lobby>> getLobbys();

    @PATCH("lobbys/{id}")
    Call<Lobby> patchCurrentMusic(@Path("id") String lobbyID, @Body Lobby lobby);
}
