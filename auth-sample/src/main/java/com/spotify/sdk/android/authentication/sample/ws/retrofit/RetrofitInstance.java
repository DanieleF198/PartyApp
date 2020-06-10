package com.spotify.sdk.android.authentication.sample.ws.retrofit;

        import android.content.ContentResolver;
        import android.net.Uri;


        import java.io.IOException;
        import java.net.Socket;

        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {

    private static Retrofit retrofit;

    /*base url*/
    private static final String BASE_URL = "http://192.168.42.206:3000/";


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
