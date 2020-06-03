package com.spotify.sdk.android.authentication.sample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartyHostActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f1afbc79a395491088b46771713b772d";
    private static final String REDIRECT_URI = "http://com.yourdomain.yourapp/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Toolbar mToolbar;
    private Lobby lobby;
    private long thisMoment;
    private Retrofit retrofit;
    private Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_host);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("DEBUG1", "Connected! Yay!");

                        lobby = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
                        Log.d("LOBBY_DEBUG: ", lobby.getGenre()+" " + lobby.getMood() +" " + lobby.getName());
                        defaultMusic(); //QUACK
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("DEBUG2", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void defaultMusic() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:track:"+lobby.getDefaultMusicID()); //non possiamo prendere la track tramite player state (che si prende da playerApi).
        lobby.setCurrentMusicID(lobby.getDefaultMusicID());
        LocalTime temp =  LocalTime.now();
        lobby.setMomentOfRefresh(temp.toString());
        /*
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TrackService trackService = retrofit.create(TrackService.class);

        Call<Track> call2 = trackService.getTrack(lobby.getCurrentMusicID());

        call2.enqueue(new Callback<Track>(){

            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if(!response.isSuccessful()) {
                    Log.d("Get track not success", response.body()+" "+response.code()+ " "+response.errorBody()); //restituisce errore 401
                    return;
                }
                track = response.body();
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.d("Get track failure", t.toString()+"");
                Toast.makeText(PartyHostActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
            }
        });

        long pippo = track.duration;
        Log.d("DURATA: ", pippo+"");*/

        PublicLobbyHomepageService publicLobbyHomepageService = RetrofitInstance.getRetrofitInstance().create(PublicLobbyHomepageService.class);
        Call<Lobby> call = publicLobbyHomepageService.patchCurrentMusic(lobby.getLobbyID(), lobby);

        call.enqueue(new Callback<Lobby>(){

            @Override
            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                if(!response.isSuccessful()) {
                    Log.d("Current Music Error", response.body()+" "+response.code()+ " "+response.errorBody());
                    return;
                }
                Log.d("GOOD", "it's gone");
                return; //dovrebbe aver fatto la patch
            }

            @Override
            public void onFailure(Call<Lobby> call, Throwable t) {
                Log.d("on failur curren update", t.toString()+"");
                Toast.makeText(PartyHostActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
