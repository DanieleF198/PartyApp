package com.spotify.sdk.android.authentication.sample;




import com.spotify.protocol.types.Track;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface SpotifyAPIService {


    @GET("tracks/{id}")
    Call<Track> getTrackById(@Header ("Authorization") String accessToken, @Path("id") String spotifyId);
}
