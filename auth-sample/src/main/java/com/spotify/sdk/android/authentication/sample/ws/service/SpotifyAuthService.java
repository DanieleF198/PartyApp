package com.spotify.sdk.android.authentication.sample.ws.service;

import com.spotify.sdk.android.authentication.sample.ws.model.UserAccess;

import java.net.URI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SpotifyAuthService {


    @FormUrlEncoded
    @Headers("Authorization: Basic ZjFhZmJjNzlhMzk1NDkxMDg4YjQ2NzcxNzEzYjc3MmQ6Nzc0ZTJlMWM1NGFmNGIzNTk2Njk0ZGZjZDI4NDRlOTk=")
    @POST("token")
    Call<UserAccess> authUserClientCredentialsFlow(@Field("grant_type") String grant_type);


    @GET("authorize")
    Call<ResponseBody> authUserImplicitGrantFlow(@Query("client_id") String client_id, @Query("response_type") String response_type, @Query("redirect_uri") String redirect_uri, @Query("scope") String scope);


}
