package com.spotify.sdk.android.authentication.sample.ws.service;




import com.google.gson.JsonObject;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.sample.ws.model.PlayResumePlayback;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SpotifyAPIService {


    @GET("tracks/{id}")
    Call<Track> getTrackById(@Header ("Authorization") String accessToken, @Path("id") String spotifyId);



    @PUT("me/player/play")
    Call<JsonObject> playUserPlayback(@Header("Authorization") String accessToken, @Body PlayResumePlayback uri);

}

