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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.ws.model.Album;
import com.spotify.sdk.android.authentication.sample.ws.model.Artist;
import com.spotify.sdk.android.authentication.sample.ws.model.CurrentlyPlayingContext;
import com.spotify.sdk.android.authentication.sample.ws.model.Image;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;
import com.spotify.sdk.android.authentication.sample.ws.model.PlayResumePlayback;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstanceSpotifyApi;
import com.spotify.sdk.android.authentication.sample.ws.service.LobbyService;
import com.spotify.sdk.android.authentication.sample.ws.service.PlayerService;
import com.spotify.sdk.android.authentication.sample.ws.service.SpotifyAPIService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoField;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

public class ClientPartyActivity extends AppCompatActivity {
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
    private long clientDelta;
    private LocalTime temp;
    private PollingCurrentMusic pollingCurrentMusic;
    private boolean isJoined;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.

        Button enterPartyButton = findViewById(R.id.joinParty);
        enterPartyButton.setText("Entra nel Party");
        isJoined = false;

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


                        enterPartyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isJoined) {

                                    LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
                                    lobbySerialized = (Lobby) getIntent().getSerializableExtra("CLIENT_LOBBY");
                                    Call<Lobby> callLobbyById = lobbyService.getLobbyById(lobbySerialized.getLobbyID());
                                    callLobbyById.enqueue(new Callback<Lobby>() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                                            Lobby lobby = response.body();
                                            assert lobby != null;
                                            Log.d("DEBUG_LOBBY: ", lobby.getCurrentMusicID() + "");
                                            if (!lobby.isOpen()) {
                                                Toast.makeText(ClientPartyActivity.this, "Il party è chiuso", Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    play(lobby, accessToken);
                                                    enterPartyButton.setText("Esci dal Party");
                                                    isJoined = true;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            //1- il client controlla che "isOpen" sia true
                                            //  1a- il client si dovrà sincronizzare con l'host e far partire la musica se current music è diverso da null,
                                            //  1b- altrimenti un Toast segnera che il party è chiuso.
                                            //2- a fine musica il client dovrà controllare di nuovo "isOpen" se è a true
                                            //  2a- se sì, controlla la current music e si sincronizza con quella (non next perché al cambio la next diventa la default e la current diventa la next)
                                            //  2b- altrimenti si mette in pausa il player (caso in cui l'host se n'è andato)
                                        }

                                        @Override
                                        public void onFailure(Call<Lobby> call, Throwable t) {
                                            Log.d("DEBUG_LOBBY_FAIL: ", t.toString() + "");
                                        }
                                    });
                                }
                                else{
                                    onBackPressed();
                                }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void play(Lobby lobby, String accessToken) throws IOException {

        LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);
        Call<Lobby> lobbyCall = lobbyService.getLobbyById(lobby.getLobbyID());
        //lobby = lobbyCall.execute().body();


        lobbyCall.enqueue(new Callback<Lobby>() {
            @Override
            public void onResponse(Call<Lobby> call, Response<Lobby> response) {

                Lobby lobbyResp = response.body();
                PlayResumePlayback uri = new PlayResumePlayback();
                String[] uris = {lobbyResp.getCurrentMusicID()};
                uri.setUris(uris);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    temp = LocalTime.now();
                }
                assert temp != null;
                clientDelta = (temp.getLong(ChronoField.MILLI_OF_DAY));
                Log.d("DEBUG_MOMENT1", lobbyResp.getMomentOfPlay()+"");
                Log.d("DEBUG_MOMENT2", clientDelta+"");

                clientDelta = clientDelta - lobbyResp.getMomentOfPlay();

                uri.setPosition_ms(clientDelta);
                Log.d("DEBUG_DELAY", clientDelta+"");

                Log.d("ARRAY CONSTRUCT", uris + "");
                SpotifyAPIService spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
                Call<JsonObject> responseCall = spotifyAPIService.playUserPlayback("Bearer " + accessToken, uri);
                responseCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("DEBUG_PLAY", response.body() + "");
                        Call<Lobby> lobbyCallParam = lobbyService.getLobbyById(lobbyResp.getLobbyID());
                        SpotifyAPIService spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
                        Call<Track> callTrack = spotifyAPIService.getTrackById("Bearer "+accessToken, lobbyResp.getCurrentMusicID().substring(14));
                        callTrack.enqueue(new Callback<Track>() {
                            @Override
                            public void onResponse(Call<Track> call, Response<Track> response) {
                                getTrackDataForView(response.body(), accessToken);
                            }

                            @Override
                            public void onFailure(Call<Track> call, Throwable t) {
                                Log.d("DEBUG_GET_TRACK_DATA: ", t.toString()+"");
                            }
                        });
                        pollingCurrentMusic = new PollingCurrentMusic();
                        pollingCurrentMusic.execute(lobbyCallParam);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("DEBUG_PLAY_FAIL", t.toString() + "");
                    }
                });

            }

            @Override
            public void onFailure(Call<Lobby> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        SpotifyAPIService spotifyAPIService;

        Button enterPartyButton = findViewById(R.id.joinParty);
        enterPartyButton.setText("Entra nel Party");
        isJoined=false;

        spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
        Call<JsonObject> callPauseUserPlayback = spotifyAPIService.pauseUserPlayback("Bearer " + accessToken);
        callPauseUserPlayback.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Player paused: ",response.code()+"");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Player paused: ",t.getMessage()+"");
            }
        });

        if(pollingCurrentMusic != null)
            pollingCurrentMusic.cancel(true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    class PollingCurrentMusic extends AsyncTask<Call<Lobby>, Void, Lobby> {


        @Override
        protected Lobby doInBackground(Call<Lobby>... param) {

            Call<Lobby> call = param[0];
            Call<Lobby> newCall;
            Lobby lobby = new Lobby();
            Lobby lobbyTemp = new Lobby();
            String currentMusicId;

            try {
                lobby = call.execute().body();
                currentMusicId = lobby.getCurrentMusicID();

                do {
                    if(isCancelled())
                        break;
                    newCall = call.clone();
                    lobbyTemp = newCall.execute().body();
                    if (newCall.isExecuted())
                        newCall.cancel();
                    Log.d("CLIENT_POLLING", lobbyTemp.getCurrentMusicID());
                } while(lobbyTemp.getCurrentMusicID().equals(lobby.getCurrentMusicID()) && lobbyTemp.isOpen());


            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("DEBUG_IS_OPEN: ", lobby.isOpen()+"");
            return lobbyTemp;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Lobby lobby) {
            Log.d("DEBUG_CLIENTPOLLING", "music changed");
            Log.d("DEBUG_IS_OPEN2: ", lobby.isOpen()+"");

            try {
                if(lobby.isOpen()) {
                    play(lobby, accessToken);
                }
                else {
                    pause();
                    ImageView imageCurrentTrack = findViewById(R.id.songImageClient);
                    TextView trackName = findViewById(R.id.nameCurrentTrackClient);
                    TextView albumName = findViewById(R.id.nameCurrentAlbumClient);
                    TextView artistName = findViewById(R.id.nameCurrentArtistClient);
                    TextView title = findViewById(R.id.titleClient);
                    Button buttonTemp = findViewById(R.id.joinParty);
                    title.setText("");
                    trackName.setText("");
                    albumName.setText("");
                    artistName.setText("");
                    buttonTemp.setText("Entra nel Party");
                    Toast.makeText(ClientPartyActivity.this, "Il Party è stato chiuso dall'host", Toast.LENGTH_SHORT).show();
                    isJoined = false;
                    if(imageCurrentTrack.getVisibility() == View.VISIBLE){
                        imageCurrentTrack.setVisibility(View.INVISIBLE);
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getTrackDataForView(Track track, String accessToken){
        ImageView imageCurrentTrack = findViewById(R.id.songImageClient);
        TextView trackName = findViewById(R.id.nameCurrentTrackClient);
        TextView albumName = findViewById(R.id.nameCurrentAlbumClient);
        TextView artistName = findViewById(R.id.nameCurrentArtistClient);
        TextView title = findViewById(R.id.titleClient);
        SpotifyAPIService serviceForData = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
        Call<Album> callForData = serviceForData.getAlbumTrack("Bearer " + accessToken, track.album.uri.substring(14));
        callForData.enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                Image[] imagesOfCurrentTrack = response.body().getImages();
                Artist[] artistsOfCurrentTrack = response.body().getArtists();
                if(imageCurrentTrack.getVisibility() == View.INVISIBLE){
                    imageCurrentTrack.setVisibility(View.VISIBLE);
                }
                Picasso.get().load(imagesOfCurrentTrack[0].getUrl()).into(imageCurrentTrack);
                title.setText("Attualmente in riproduzione:");
                trackName.setText(track.name);
                albumName.setText(response.body().getName());
                artistName.setText((artistsOfCurrentTrack[0].getName()));
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {

            }
        });
    }

    public void pause(){
        SpotifyAPIService spotifyAPIService;
        spotifyAPIService = RetrofitInstanceSpotifyApi.getRetrofitInstance().create(SpotifyAPIService.class);
        Call<JsonObject> callPauseUserPlayback = spotifyAPIService.pauseUserPlayback("Bearer " + accessToken);
        callPauseUserPlayback.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Player paused: ",response.code()+"");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Player paused: ",t.getMessage()+"");
            }
        });
    }
}
