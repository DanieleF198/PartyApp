/*
 * Copyright (c) 2015-2018 Spotify AB
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.sdk.android.authentication.sample.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.adapter.LobbyAdapter;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.model.UserRemember;
import com.spotify.sdk.android.authentication.sample.ws.service.LobbyService;
import com.spotify.sdk.android.authentication.sample.ws.service.UserService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f1afbc79a395491088b46771713b772d";
    private static final String REDIRECT_URI = "http://com.yourdomain.yourapp/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Toolbar mToolbar;
    private Track track;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<Lobby> passedList;
    private List<Lobby> lobbyList;
    private Intent intent;
    private Gson gson;
    private Fragment selectedFragment;
    private Fragment selectedFragment2;
    private Fragment selectedFragment3;
    private Fragment currentFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment selectedFragment = new HomepageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        currentFrag = selectedFragment;

        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG_LEFT_MENU", view+"");
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();


        UserService service = RetrofitInstance.getRetrofitInstance().create(UserService.class);
        Log.d("PACKAGE_NAME", getApplicationContext().getPackageName()+"");
        Log.d("DEBUG_BUNDLE: ",bundle+"");

        LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);

        Call<List<Lobby>> call = lobbyService.getLobbys();

        call.enqueue(new Callback<List<Lobby>>() {
            @Override
            public void onResponse(Call<List<Lobby>> call, Response<List<Lobby>> response) {
                if(!response.isSuccessful()){
                    Log.d("Lobby Not Success", "some error");
                    return;
                }
                List<Lobby> lobbys = response.body();
                Collections.sort(lobbys);
                recyclerFunction(lobbys);
            }

            @Override
            public void onFailure(Call<List<Lobby>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
            }
        });


        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG_MENU", "fab");
                LobbyService lobbyService2 = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);

                Call<List<Lobby>> call2 = lobbyService2.getLobbys();

                call2.enqueue(new Callback<List<Lobby>>() {
                    @Override
                    public void onResponse(Call<List<Lobby>> call, Response<List<Lobby>> response) {
                        if(!response.isSuccessful()){
                            Log.d("Lobby Not Success", "some error");
                            return;
                        }
                        List<Lobby> lobbys = response.body();
                        Collections.sort(lobbys);
                        recyclerFunction(lobbys);
                    }

                    @Override
                    public void onFailure(Call<List<Lobby>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG_GET_LOBBYS",t.getMessage().toString());
                    }
                });

                Fragment selectedFragment3 = new HomepageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment3).commit();
                currentFrag = selectedFragment3;
            }
        });

    }

    public void recyclerFunction(List<Lobby> passedList){
        boolean fileExists = true; //se è arrivato nella main activity vuol dire che siamo loggati
        String defaultJson = "{\"id\":\"\",\"username\":\"\",\"password\":\"\",\"remember\":false}";
        gson = new Gson();

        String idRemember = "";

        String[] files = getApplicationContext().fileList();

        for(String s : files) {
            if (s.equals("remember.json")) {

                FileInputStream fis = null;
                try {
                    fis = getApplicationContext().openFileInput("remember.json");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                InputStreamReader inputStreamReader =
                        new InputStreamReader(fis, StandardCharsets.UTF_8);
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line = reader.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append('\n');
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    // Error occurred when opening raw file for reading.
                } finally {
                    String contents = stringBuilder.toString();
                    Log.d("STRING_BUILDER_DEBUG", stringBuilder.toString() + "");
                    UserRemember userRemember = gson.fromJson(contents, UserRemember.class);
                    Log.d("userRememberDEBUG", userRemember.getId() + " " + userRemember.getUsername() + " " + userRemember.getPassword() + " " + userRemember.isRemember() + " ");
                    if (userRemember.isRemember()) {
                        idRemember = userRemember.getId();
                    }
                }
            }
        }


        for(Iterator<Lobby> iterator = passedList.iterator(); iterator.hasNext();){
            Lobby l = iterator.next();
            if((!l.getPublicType()) || (l.getHostID().equals(idRemember))){
                iterator.remove();
            }
        }
        recyclerView = findViewById(R.id.recyclerViewLobby);
        recyclerView.setHasFixedSize(true);
        layoutManager =  new LinearLayoutManager(this);
        adapter = new LobbyAdapter(passedList, getApplicationContext(), new LobbyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lobby lobby) {
                // Toast.makeText(getContext(), "Item Clicked : "+risultato.getId(), Toast.LENGTH_LONG).show();
                intent = new Intent(getApplication(), ClientPartyActivity.class);
                intent.putExtra("CLIENT_LOBBY", lobby);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    public void test(){
        CallResult<PlayerState> callTrack =  mSpotifyAppRemote.getPlayerApi().getPlayerState();
        callTrack.setResultCallback((playerState) -> {Log.d("DEBUG_TRACK_NAME", " "+playerState.track.name);});
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

                        // Now you can start interacting with App Remote
                        connected();
                        test();

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("DEBUG2", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected() {
        // Then we will write some more code here.
        mSpotifyAppRemote.getPlayerApi().play("spotify:track:3albRz1vGpxGo5xSnGirhk");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        Log.d("DEBUG_MENU", menu.toString()+"");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("ITEM_PRESSED",item.getItemId()+"");
        switch (item.getItemId()){
            case R.id.logout:
                Context context = MainActivity.this;
                new AlertDialog.Builder(context)
                        .setTitle("Logout")
                        .setMessage("Sei sicuro di voler effettuare il logout?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LOGOUT_PRESSED_MENU","logout pressed");

                                Intent intentToLoginActivity = new Intent(getApplicationContext(), LoginActivity.class );
                                Gson gson = new Gson();
                                UserRemember userRemember = new UserRemember("", "","", false);
                                String json = gson.toJson(userRemember);
                                try (FileOutputStream fos = getApplicationContext().openFileOutput("remember.json", Context.MODE_PRIVATE)) {
                                    fos.write(json.getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intentToLoginActivity);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;

            case R.id.myLobbies:
                LobbyService lobbyService = RetrofitInstance.getRetrofitInstance().create(LobbyService.class);

                Call<List<Lobby>> call = lobbyService.getLobbys();

                call.enqueue(new Callback<List<Lobby>>() {
                    @Override
                    public void onResponse(Call<List<Lobby>> call, Response<List<Lobby>> response) {
                        if(!response.isSuccessful()){
                            Log.d("Lobby Not Success", "some error");
                            return;
                        }
                        List<Lobby> lobbys = response.body();
                        Collections.sort(lobbys);
                        recyclerFunction2(lobbys);
                    }

                    @Override
                    public void onFailure(Call<List<Lobby>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG_GET_LOBBYS",t.getMessage().toString());
                    }
                });

                Fragment selectedFragment2 = new MyLobbiesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment2).commit();
                currentFrag = selectedFragment2;

                return true;

            default:
                Log.d("DEBUG_MENU", "default");
                return super.onOptionsItemSelected(item);
        }
    }

    public void recyclerFunction2(List<Lobby> passedList){ //avresti ragione a dirci che potevamo anche usare il bundle, ma ce ne siamo ricordati mentre stavamo a fa sto codice e siccome tanto è la stessa cosa amen. DR e DF

        boolean fileExists = true; //se è arrivato nella main activity vuol dire che siamo loggati
        String defaultJson = "{\"id\":\"\",\"username\":\"\",\"password\":\"\",\"remember\":false}";
        gson = new Gson();

        String idRemember = "";

        String[] files = getApplicationContext().fileList();

        for(String s : files) {
            if (s.equals("remember.json")) {

                FileInputStream fis = null;
                try {
                    fis = getApplicationContext().openFileInput("remember.json");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                InputStreamReader inputStreamReader =
                        new InputStreamReader(fis, StandardCharsets.UTF_8);
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line = reader.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append('\n');
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    // Error occurred when opening raw file for reading.
                } finally {
                    String contents = stringBuilder.toString();
                    Log.d("STRING_BUILDER_DEBUG", stringBuilder.toString() + "");
                    UserRemember userRemember2 = gson.fromJson(contents, UserRemember.class);
                    Log.d("userRememberDEBUG", userRemember2.getId() + " " + userRemember2.getUsername() + " " + userRemember2.getPassword() + " " + userRemember2.isRemember() + " ");
                    if (userRemember2.isRemember()) {
                        idRemember = userRemember2.getId();
                    }
                }
            }
        }
        for (Iterator<Lobby> iterator = passedList.iterator(); iterator.hasNext(); ) {
            Lobby l = iterator.next();
            if (!l.getHostID().equals(idRemember)) {
                iterator.remove();
            }
        }
        recyclerView = findViewById(R.id.recyclerViewLobby);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new LobbyAdapter(passedList, getApplicationContext(), new LobbyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lobby lobby) {
                Log.d("LOBBY_DEBUG1: ", lobby.getGenre()+ " " + lobby.getMood() + " " + lobby.getName());

                // Toast.makeText(getContext(), "Item Clicked : "+risultato.getId(), Toast.LENGTH_LONG).show();
                intent = new Intent(getApplication(), PartyHostActivity.class);
                intent.putExtra("HOST_LOBBY", lobby);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        if(currentFrag instanceof MyLobbiesFragment) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else{
            Context context = MainActivity.this;
            new AlertDialog.Builder(context)
                    .setTitle("Ehy, dove vai?!")
                    .setMessage("Sei sicuro di voler uscire dall'app?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("LOGOUT_PRESSED_MENU","logout pressed");
                            finishAffinity();
                            System.exit(0);
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
