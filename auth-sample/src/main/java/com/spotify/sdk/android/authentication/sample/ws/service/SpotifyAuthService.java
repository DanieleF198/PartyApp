package com.spotify.sdk.android.authentication.sample.ws.service;

import com.spotify.sdk.android.authentication.sample.ws.model.UserAccess;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SpotifyAuthService {


    @FormUrlEncoded
    @Headers("Authorization: Basic ZjFhZmJjNzlhMzk1NDkxMDg4YjQ2NzcxNzEzYjc3MmQ6Nzc0ZTJlMWM1NGFmNGIzNTk2Njk0ZGZjZDI4NDRlOTk=")
    @POST("token")
    Call<UserAccess> authUser(@Field("grant_type") String grant_type);

}
