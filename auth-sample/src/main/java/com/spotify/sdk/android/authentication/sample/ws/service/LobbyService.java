package com.spotify.sdk.android.authentication.sample.ws.service;

import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface LobbyService {

    @GET("lobbys")
    Call<List<Lobby>> getLobbys();

    @GET("lobbys/{id}")
    Call<Lobby> getLobbyById(@Path("id") String lobbyID);

    @PATCH("lobbys/{id}")
    Call<Lobby> patchLobby(@Path("id") String lobbyID, @Body Lobby lobby);

}
