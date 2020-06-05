package com.spotify.sdk.android.authentication.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.adapter.LobbyAdapter;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;
import com.spotify.sdk.android.authentication.sample.ws.model.UserRemember;
import com.spotify.sdk.android.authentication.sample.ws.service.LobbyService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserLobbyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Gson gson;


    private List<Lobby> lobbyList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_lobby_homepage);


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
                Toast.makeText(UserLobbyActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG_GET_LOBBYS",t.getMessage().toString());
            }
        });

    }
    public void recyclerFunction(List<Lobby> passedList){ //avresti ragione a dirci che potevamo anche usare il bundle, ma ce ne siamo ricordati mentre stavamo a fa sto codice e siccome tanto è la stessa cosa amen. DR e DF

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

}