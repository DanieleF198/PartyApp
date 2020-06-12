package com.spotify.sdk.android.authentication.sample.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.ws.model.CurrentlyPlayingContext;
import com.spotify.sdk.android.authentication.sample.ws.model.PlayResumePlayback;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstanceSpotifyApi;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;
import com.spotify.sdk.android.authentication.sample.ws.service.LobbyService;
import com.spotify.sdk.android.authentication.sample.ws.service.PlayerService;
import com.spotify.sdk.android.authentication.sample.ws.service.SpotifyAPIService;


import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoField;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

public class PartyHostActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f1afbc79a395491088b46771713b772d";
    private static final String REDIRECT_URI = "http://com.yourdomain.yourapp/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Toolbar mToolbar;
    private Lobby lobbySerialized;
    private long thisMoment;
    private Retrofit retrofit;
    private Track track;
    private long duration;
    private String uri;
    private String authorizationHeader;
    private ConnectionParams connectionParams;
    private Button endVoteButton;
    private static final int REQUEST_CODE = 1337;
    private String accessToken;
    private PollingPlaybackState pollingPlaybackState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_host);


    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.

        Button openPartyButton = findViewById(R.id.openParty);
        endVoteButton = findViewById(R.id.endVote);


        connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        /* Auth */
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-playback-state", "user-modify-playback-state"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);


        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {


                        openPartyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
                                        lobbySerialized = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
                                        Call<Lobby> callLobbyById = lobbyService.getLobbyById(lobbySerialized.getLobbyID());
                                        callLobbyById.enqueue(new Callback<Lobby>() {
                                            @Override
                                            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                                                Lobby lobby = response.body();
                                                lobby.setCurrentMusicID(lobby.getDefaultMusicID());
                                                SpotifyAPIService spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
                                                Call<Track> callTrack = spotifyAPIService.getTrackById("Bearer "+accessToken, lobby.getCurrentMusicID().substring(14));
                                                callTrack.enqueue(new Callback<Track>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onResponse(Call<Track> call, Response<Track> response) {
                                                        track = response.body();
                                                        LocalTime temp = null;
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            temp = LocalTime.now();
                                                        }
                                                        assert temp != null;
                                                        lobby.setMomentOfPlay(temp.getLong(ChronoField.MILLI_OF_DAY));
                                                        lobby.setMusicDuration(track.duration);
                                                        lobby.setNextMusicID(lobby.getDefaultMusicID());
                                                        lobby.setOpen(true);
                                                        patchLobby(lobby);
                                                        play(lobby, accessToken);

                                                    }

                                                    @Override
                                                    public void onFailure(Call<Track> call, Throwable t) {
                                                        Log.d("DEBUG", "DOVEVA anna COSì FRATELLì");
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onFailure(Call<Lobby> call, Throwable t) {

                                    }
                                });

                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d("DEBUG2", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Log.d("AUTH_DONE", response.getAccessToken()+"");
                    accessToken = response.getAccessToken();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    class PollingPlaybackState extends AsyncTask<Call<CurrentlyPlayingContext>, Lobby, Void>{


        @Override
        protected Void doInBackground(Call<CurrentlyPlayingContext>... param) {

            Call<CurrentlyPlayingContext> call = param[0];
            Call<CurrentlyPlayingContext> newCall;
            CurrentlyPlayingContext currentlyPlayingContextTemp;
            CurrentlyPlayingContext currentlyPlayingContext;
            int durationTrack;



            try {

                currentlyPlayingContext = call.execute().body();
                //Response<CurrentlyPlayingContext> response = call.execute();
                //Log.d("RESPONSE.CODE", response.message()+"");
                //Log.d("RESPONSE.CODE", response.body().getItem()+"");
                durationTrack = currentlyPlayingContext.getItem().getDuration_ms();
                Log.d("DURATION_TRACK", durationTrack+"");

                do {
                    if(isCancelled())
                        break;
                    newCall = call.clone();
                    currentlyPlayingContextTemp = newCall.execute().body();
                    if (newCall.isExecuted())
                        newCall.cancel();

                    if(currentlyPlayingContextTemp == null) {
                        currentlyPlayingContextTemp = new CurrentlyPlayingContext();
                        currentlyPlayingContextTemp.setProgress_ms(0);
                        Log.d("CURRENTLYPLAYINGCONT", currentlyPlayingContextTemp.getProgress_ms()+"--> Doveva anna cosi fratellì");
                    }else
                        Log.d("CURRENTLYPLAYINGCONT", currentlyPlayingContextTemp.getProgress_ms() + "--> QUACK");

        }while (currentlyPlayingContextTemp.getProgress_ms() < durationTrack-3500);
    } catch (IOException e) {
        e.printStackTrace();
    }

            return  null;
}



    @Override
    protected void onPostExecute(Void aVoid) {
            LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
            lobbySerialized = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
            Call<Lobby> callLobbyById = lobbyService.getLobbyById(lobbySerialized.getLobbyID());
            callLobbyById.enqueue(new Callback<Lobby>() {
                @Override
                public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                    Lobby lobby = response.body();
                    lobby.setCurrentMusicID(lobby.getNextMusicID());
                    lobby.setNextMusicID(lobby.getDefaultMusicID()); //nel caso in cui non venga cliccato endVote riparte la musica di default finché non viene cliccato end vote
                    SpotifyAPIService spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
                    Call<Track> callTrack = spotifyAPIService.getTrackById("Bearer "+accessToken, lobby.getCurrentMusicID().substring(14));
                    callTrack.enqueue(new Callback<Track>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call<Track> call, Response<Track> response) {
                            track = response.body();
                            LocalTime temp = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                temp = LocalTime.now();
                            }
                            assert temp != null;
                            lobby.setMomentOfPlay(temp.getLong(ChronoField.MILLI_OF_DAY));
                            lobby.setMusicDuration(track.duration);
                            patchLobby(lobby);
                            play(lobby, accessToken);

                        }

                        @Override
                        public void onFailure(Call<Track> call, Throwable t) {
                            Log.d("DEBUG", "DOVEVA anna COSì FRATELLì");
                        }
                    });
                }
                @Override
                public void onFailure(Call<Lobby> call, Throwable t) {
                }
            });
        }
    }



    private void patchLobby(Lobby lobbyToPatch){
        Log.d("DEBUG_PATCH1", "siamo quiii");
        LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
        Call<Lobby> callPatchLobby = lobbyService.patchLobby(lobbyToPatch.getLobbyID(), lobbyToPatch);
        callPatchLobby.enqueue(new Callback<Lobby>() {

            @Override
            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                Log.d("DEBUG_PATCH_DONE", "");
            }
            @Override
            public void onFailure(Call<Lobby> call, Throwable t) {
                Log.d("DEBUG_PATCH_FAIL", t+"");
            }
        });
    }

    private void play(Lobby lobby, String accessToken) {
        PlayResumePlayback uri = new PlayResumePlayback();
        String[] uris = {lobby.getCurrentMusicID()};
        uri.setUris(uris);

        Log.d("ARRAY CONSTRUCT", uris + "");
        SpotifyAPIService spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
        Call<JsonObject> responseCall = spotifyAPIService.playUserPlayback("Bearer " + accessToken, uri);
        responseCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("DEBUG_PLAY", response.body() + "");
                //Player Service
                PlayerService playerService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(PlayerService.class);
                Call<CurrentlyPlayingContext> callGetPlayer = playerService.getInfoCurrentUserPlayback("Bearer " + accessToken);
                //Start AsyncTask
                pollingPlaybackState = new PollingPlaybackState();
                endVoteButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Thread setNextMusic = new Thread() {
                            @Override
                            public void run() {
                                Log.d("TEST", "mi hai cliccato");
                                lobby.setNextMusicID("spotify:track:0jSS8qyuv7DdkAaGSmmHv5");
                                patchLobby(lobby);
                            }
                        };
                        setNextMusic.start();
                    }
                }); //endVote
                pollingPlaybackState.execute(callGetPlayer);
                //End Player Service
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("DEBUG_PLAY_FAIL", t.toString() + "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
        lobbySerialized = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
        Call<Lobby> callLobbyById = lobbyService.getLobbyById(lobbySerialized.getLobbyID());
        callLobbyById.enqueue(new Callback<Lobby>() {
            @Override
            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                Log.d("DEBUG_ONBACK", response.body()+"");
                Lobby lobby = response.body();
                assert lobby != null;
                lobby.setOpen(false);
                Log.d("DEBUG_ONBACK_LOBBY: ", lobby.getLobbyID()+"");
                LobbyService lobbyService2 = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
                Call<Lobby> callPatchLobby = lobbyService2.patchLobby(lobby.getLobbyID(), lobby);
                callPatchLobby.enqueue(new Callback<Lobby>() {

                    @Override
                    public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                        Log.d("DEBUG_PATCH_DONE", "");
                    }
                    @Override
                    public void onFailure(Call<Lobby> call, Throwable t) {
                        Log.d("DEBUG_PATCH_FAIL", t+"");
                    }
                });

            }
            @Override
            public void onFailure(Call<Lobby> call, Throwable t) {
                Log.d("DEBUG_ONBACK_FAIL", t.toString()+"");
            }
        });
        pollingPlaybackState.cancel(true);
        Intent intent = new Intent(this, UserLobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}





    /*class PlayMusic extends AsyncTask<Lobby, Void, Void> {


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Lobby... params) {

            Lobby lobby = (Lobby) params[0];
            assert lobby != null;
            lobby.setCurrentMusicID(track.uri); //lobby.getDefaultMusicID() è usata per test perchè è una track corta
            LocalTime time = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                time = LocalTime.now();
            }
            assert time != null;
            lobby.setMomentOfPlay(time.getLong(ChronoField.HOUR_OF_DAY)+time.getLong(ChronoField.MINUTE_OF_DAY)+time.getLong(ChronoField.SECOND_OF_DAY)+time.getLong(ChronoField.MILLI_OF_DAY));
            lobby.setMusicDuration(track.duration);


            LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
            Call<Lobby> callPatchLobby = lobbyService.patchLobby(lobby.getLobbyID(), lobby);
            callPatchLobby.enqueue(new Callback<Lobby>() {
                @Override
                public void onResponse(Call<Lobby> call, Response<Lobby> response) {

                    //play music
                    play(response.body());

                    *//*endVoteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Thread setNextMusic = new Thread() {
                                @Override
                                public void run() {

                                    lobby.setNextMusicID("spotify:track:6c2UkrZuNlLkZ7VyYcOw3V");

                                    patchLobby(lobby);

                                }
                            };

                            setNextMusic.start();
                        }
                    }); //endVote*//*

                    DisableSkipNext disableSkipNext = new DisableSkipNext();
                    disableSkipNext.execute();

                    //siamo in riproduzione
                    //va disattivato skipNext
                    //come finisce la musica in riproduzione si ferma
                    //decide l'host quando far partire la nextMusic con un pulsante
                    //due opzioni o la next parte dalla queue (rimettendo skipNext a true) o si fa partire in play() direttamente.

*//*
                    LobbyService lobbyServiceSync = ServiceGenerator.createService(LobbyService.class);
                    Call<Lobby> callGetLobby = lobbyServiceSync.getLobbyById(lobby.getLobbyID());
                    try {
                        Lobby tempLobby = null;
                         tempLobby = callGetLobby.execute().body();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Log.d("DEBUG_tempLobby", tempLobby.getDefaultMusicID());

                    SpotifyAppRemote.connect(getApplicationContext(), connectionParams,
                            new Connector.ConnectionListener() {

                                @Override
                                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                    mSpotifyAppRemote = spotifyAppRemote;
                                    Log.d("DEBUG1", "Connected! Yay!");


                                    // Now you can start interacting with App Remote

                                        CallResult<Empty> playerStateCallResult = mSpotifyAppRemote.getPlayerApi().queue(lobby.getNextMusicID());

                                }

                                @Override
                                public void onFailure(Throwable throwable) {

                                }
                            });
*//*
                }

                @Override
                public void onFailure(Call<Lobby> call, Throwable t) {}
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("ONPOSTEXECUTE", "FINISHED MUSIC");
        }

        private void play(Lobby lobby){

            SpotifyAppRemote.connect(getApplicationContext(), connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.d("DEBUG1", "Connected! Yay!");

                            // Now you can start interacting with App Remote
                            mSpotifyAppRemote.getPlayerApi().play(lobby.getCurrentMusicID());
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    });

        }
    }


    class DisableSkipNext extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SpotifyAppRemote.connect(getApplicationContext(), connectionParams,
                    new Connector.ConnectionListener() {
                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {

                            CallResult<PlayerState> playerStateCall = spotifyAppRemote.getPlayerApi().getPlayerState();
                            Result<PlayerState> playerStateResult = playerStateCall.await(40, TimeUnit.SECONDS);
                            if (playerStateResult.isSuccessful()) {
                                PlayerState playerState = playerStateResult.getData();
                                // have some fun with playerState
                            } else {
                                Throwable error = playerStateResult.getError();
                                // try to have some fun with the error
                            }

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    });

            return null;
        }
    }*/