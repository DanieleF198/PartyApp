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
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private long duration;
    private String uri;

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

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("DEBUG1", "Connected! Yay!");

                        lobby = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
                        Log.d("LOBBY_DEBUG: ", lobby.getGenre()+" " + lobby.getMood() +" " + lobby.getName());

                        CallResult<PlayerState> callTrack =  mSpotifyAppRemote.getPlayerApi().getPlayerState();
                        callTrack.setResultCallback((playerState) -> {Log.d("DEBUG_PLAYER_STATE",playerState.track.duration+"");});

                        defaultMusic();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("DEBUG2", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }


    private void defaultMusic() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:track:"+lobby.getDefaultMusicID()); //non possiamo prendere la track tramite player state (che si prende da playerApi).
        lobby.setCurrentMusicID(lobby.getDefaultMusicID());
        LocalTime temp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            temp = LocalTime.now();
        }
        lobby.setMomentOfRefresh(temp.toString()); //meglio chiamarlo of play

        CallResult<PlayerState> callTrack =  mSpotifyAppRemote.getPlayerApi().getPlayerState();
        callTrack.setResultCallback((playerState) -> {duration = playerState.track.duration;});

        lobby.setMusicDuration(duration);

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
                //dato il tempo che abbiamo già, usare un timer che dopo quel valore fa partire un TimerTask
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        CallResult<PlayerState> callTrack =  mSpotifyAppRemote.getPlayerApi().getPlayerState();
                        callTrack.setResultCallback((playerState) -> {uri = playerState.track.uri;});

                        lobby.setCurrentMusicID(uri);
                        LocalTime temp = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            temp = LocalTime.now();
                        }
                        lobby.setMomentOfRefresh(temp.toString());

                        PublicLobbyHomepageService publicLobbyHomepageService = RetrofitInstance.getRetrofitInstance().create(PublicLobbyHomepageService.class);
                        Call<Lobby> callTask = publicLobbyHomepageService.patchCurrentMusic(lobby.getLobbyID(), lobby);

                        CallResult<PlayerState> callTrack2 =  mSpotifyAppRemote.getPlayerApi().getPlayerState();
                        callTrack.setResultCallback((playerState) -> {duration = playerState.track.duration;});

                        lobby.setMusicDuration(duration);

                        callTask.enqueue(new Callback<Lobby>(){

                            @Override
                            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                                if(!response.isSuccessful()) {
                                    Log.d("Current Music Error", response.body()+" "+response.code()+ " "+response.errorBody());
                                    return;
                                }
                                Log.d("QUACK", "it's gone");
                                return; //dovrebbe aver fatto la patch
                            }

                            @Override
                            public void onFailure(Call<Lobby> call, Throwable t) {
                                Log.d("onFailureUpdate", t.toString()+"");
                                Toast.makeText(PartyHostActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };


                Timer t = new Timer("timerOfDefault");
                timeDown(t, timerTask, duration);
            }

            @Override
            public void onFailure(Call<Lobby> call, Throwable t) {
                Log.d("on failur curren update", t.toString()+"");
                Toast.makeText(PartyHostActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void timeDown(Timer timer, TimerTask timerTask, long delay ){
        Log.d("DEBUG_IN_TIMEDOWN", timerTask+"");
        timer.schedule(timerTask, delay);

        CallResult<PlayerState> callTrack =  mSpotifyAppRemote.getPlayerApi().getPlayerState();
        callTrack.setResultCallback((playerState) -> {duration = playerState.track.duration;});

        timeDown(timer, timerTask, duration);
    }
}
