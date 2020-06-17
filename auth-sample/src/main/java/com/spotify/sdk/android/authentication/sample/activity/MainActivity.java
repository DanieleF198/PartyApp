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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.model.UserRemember;
import com.spotify.sdk.android.authentication.sample.ws.service.UserService;

import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f1afbc79a395491088b46771713b772d";
    private static final String REDIRECT_URI = "http://com.yourdomain.yourapp/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private Toolbar mToolbar;
    private Track track;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();


        UserService service = RetrofitInstance.getRetrofitInstance().create(UserService.class);
        Log.d("PACKAGE_NAME", getApplicationContext().getPackageName()+"");
        Log.d("DEBUG_BUNDLE: ",bundle+"");

        Intent intentToLoginActivity = new Intent(this, LoginActivity.class );


        Button gotoLoginButton = findViewById(R.id.gotologin);
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);


        gotoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });

        Button gotoUserLobbyListButton = findViewById(R.id.gotomylobbys);

        gotoUserLobbyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this,UserLobbyActivity.class);
                view.getContext().startActivity(intent2);
            }
        });

        Button gotoListsButton = findViewById(R.id.gotoLists);

        gotoListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PublicLobbyHomepageActivity.class);
                view.getContext().startActivity(intent);
            }
        });

    }


    //Options menu
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }*/

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
}
