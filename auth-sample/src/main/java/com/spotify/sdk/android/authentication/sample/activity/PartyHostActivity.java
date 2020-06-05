package com.spotify.sdk.android.authentication.sample.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstanceSpotifyApi;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstanceSpotifyAuthApi;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;
import com.spotify.sdk.android.authentication.sample.ws.model.UserAccess;
import com.spotify.sdk.android.authentication.sample.ws.model.UserAccessPost;
import com.spotify.sdk.android.authentication.sample.ws.service.LobbyService;
import com.spotify.sdk.android.authentication.sample.ws.service.SpotifyAPIService;
import com.spotify.sdk.android.authentication.sample.ws.service.SpotifyAuthService;

import java.time.LocalTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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


        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {

                        openPartyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                SpotifyAuthService spotifyAuthService = RetrofitInstanceSpotifyAuthApi.getRetrofitInstance().create(SpotifyAuthService.class);

                                UserAccessPost userAccessPost = new UserAccessPost();
                                userAccessPost.setGrant_type("client_credentials");
                                Call<UserAccess> callAuth = spotifyAuthService.authUser(userAccessPost.getGrant_type());

                                callAuth.enqueue(new Callback<UserAccess>() {
                                    @Override
                                    public void onResponse(Call<UserAccess> call, Response<UserAccess> response) {

                                        Log.d("DEBUG_AUTH_RESP ",response.body().getToken_type()+" "+response.body().getAccess_token()+"");

                                        SpotifyAPIService spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
                                        Call<Track> callTrack = spotifyAPIService.getTrackById(response.body().getToken_type()+" "+response.body().getAccess_token(), "4wrtmc39a5N0iy1MRaaEeT");

                                        callTrack.enqueue(new Callback<Track>() {
                                            @Override
                                            public void onResponse(Call<Track> call, Response<Track> response) {

                                                Log.d("DEBUG_TRACK_RESP: ",response.body().name+"");
                                                track = response.body();
                                                LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
                                                lobbySerialized = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
                                                Call<Lobby> callLobbyById = lobbyService.getLobbyById(lobbySerialized.getLobbyID());
                                                callLobbyById.enqueue(new Callback<Lobby>() {
                                                    @Override
                                                    public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                                                        Lobby lobby = response.body();

                                                        assert lobby != null;
                                                        lobby.setCurrentMusicID(lobby.getDefaultMusicID()); //lobby.getDefaultMusicID() è usata per test perchè è una track corta
                                                        LocalTime temp = null;
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            temp = LocalTime.now();
                                                        }
                                                        assert temp != null;
                                                        lobby.setMomentOfPlay(temp.toString());
                                                        lobby.setMusicDuration(track.duration);

                                                        Call<Lobby> callPatchLobby = lobbyService.patchLobby(lobby.getLobbyID(),lobby);
                                                        callPatchLobby.enqueue(new Callback<Lobby>() {
                                                            @Override
                                                            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                                                                Log.d("DEBUG_PATCH_LOBBY ",response.body().getMusicDuration()+"");
                                                            }

                                                            @Override
                                                            public void onFailure(Call<Lobby> call, Throwable t) {

                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onFailure(Call<Lobby> call, Throwable t) {

                                                    }
                                                });





                                            }

                                            @Override
                                            public void onFailure(Call<Track> call, Throwable t) {
                                                Log.d("DEBUG_TRACK_FAIL: ",t.getMessage().toString()+"");
                                            }
                                        });


                                    }

                                    @Override
                                    public void onFailure(Call<UserAccess> call, Throwable t) {
                                        Log.d("DEBUG_AUTH_FAIL: ","FAIL");
                                    }
                                });

                                /*
                                lobby = (Lobby) getIntent().getSerializableExtra("HOST_LOBBY");
                                lobby.setCurrentMusicID(lobby.getDefaultMusicID());
                                LocalTime temp = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    temp = LocalTime.now();
                                }
                                lobby.setMomentOfPlay(temp.toString());
                                CallResult<PlayerState> callTrack =  mSpotifyAppRemote.getPlayerApi().getPlayerState();

                                PublicLobbyHomepageService publicLobbyHomepageService = RetrofitInstance.getRetrofitInstance().create(PublicLobbyHomepageService.class);

                                Call<Lobby> call = publicLobbyHomepageService.patchCurrentMusic(lobby.getLobbyID(), lobby);
                                */

                            }
                        });
                        mSpotifyAppRemote = spotifyAppRemote;


                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d("DEBUG2", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }


}
