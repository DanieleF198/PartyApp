package com.spotify.sdk.android.authentication.sample;

import android.content.ContentResolver;
import android.net.Uri;


import java.io.IOException;
import java.net.Socket;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstanceSpotifyApi {

    private static Retrofit retrofit;

    /*base url*/
    private static final String BASE_URL = "https://api.spotify.com/v1/";


    /**
     * Create an instance of Retrofit object
     * */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}