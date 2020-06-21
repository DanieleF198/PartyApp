package com.spotify.sdk.android.authentication.sample.ws.service;




import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.sample.ws.model.Album;
import com.spotify.sdk.android.authentication.sample.ws.model.PlayResumePlayback;
import com.spotify.sdk.android.authentication.sample.ws.model.Playlist;


import org.json.JSONObject;

import java.util.ArrayList;

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

    @PUT("me/player/pause")
    Call<JsonObject> pauseUserPlayback(@Header("Authorization") String accessToken);

    @GET("playlists/{playlist_id}")
    Call<Playlist> getPlaylistTracks(@Header ("Authorization") String accessToken, @Path("playlist_id") String playlist_id);

    @GET("albums/{id}")
    Call<Album> getAlbumTrack(@Header("Authorization") String accessToken, @Path("id") String album_id);
/*
    @PUT("me/player/play")
    Call<JsonObject> playUserPlayback(@Header("Authorization") String accessToken, @Body PlayResumePlayback uri, long position_ms);*/

}

