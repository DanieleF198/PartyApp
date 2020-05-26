package com.spotify.sdk.android.authentication.sample;

import android.content.ContentResolver;
import android.net.Uri;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;

    private static final String BASE_URL = "http://127.0.0.1:3000/";

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
