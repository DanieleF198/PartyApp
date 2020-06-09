package com.spotify.sdk.android.authentication.sample.ws.retrofit;

        import android.content.ContentResolver;
        import android.net.Uri;


        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;

        import java.io.IOException;
        import java.net.Socket;

        import okhttp3.OkHttpClient;
        import okhttp3.logging.HttpLoggingInterceptor;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstanceSpotifyAuthApi {

    private static Retrofit retrofit;

    /*base url*/
    private static final String BASE_URL = "https://accounts.spotify.com/";


    /**
     * Create an instance of Retrofit object
     * */
    public static Retrofit getRetrofitInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new MyInterceptor()).build();
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}