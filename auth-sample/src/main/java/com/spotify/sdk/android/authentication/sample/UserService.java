package com.spotify.sdk.android.authentication.sample;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface UserService {

    @GET("posts")
    Call<List<User>> getUsers();

    @GET("posts/{id}")
    Call<User> getUserById(@Path("id") String userID);

    @PATCH("posts/{id}")
    Call<User> patchUser(@Path("id") String id, @Body User user);
}
