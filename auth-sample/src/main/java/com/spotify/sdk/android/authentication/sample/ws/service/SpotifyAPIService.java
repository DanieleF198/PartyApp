package com.spotify.sdk.android.authentication.sample.ws.service;




import com.google.gson.JsonObject;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.sample.ws.model.Uri;


import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpotifyAPIService {


    @GET("tracks/{id}")
    Call<Track> getTrackById(@Header ("Authorization") String accessToken, @Path("id") String spotifyId);



    @PUT("me/player/play")
    Call<JsonObject> playUserPlayback(@Header("Authorization") String accessToken, @Body Uri uri);

}

