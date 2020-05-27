package com.spotify.sdk.android.authentication.sample;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LoginService {

    @GET("posts")
    Call<List<User>> getUsers();
}
