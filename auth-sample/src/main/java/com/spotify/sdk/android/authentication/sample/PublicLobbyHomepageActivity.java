package com.spotify.sdk.android.authentication.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class PublicLobbyHomepageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    private List<Lobby> lobbyList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_lobby_homepage);

        PublicLobbyHomepageService publicLobbyHomepageService = RetrofitInstance.getRetrofitInstance().create(PublicLobbyHomepageService.class);

        Call<List<Lobby>> call = publicLobbyHomepageService.getLobbys();

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
                Toast.makeText(PublicLobbyHomepageActivity.this, "lobbysError", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void recyclerFunction(List<Lobby> passedList){
        for(Iterator<Lobby> iterator = passedList.iterator(); iterator.hasNext();){
            Lobby l = iterator.next();
            if(!l.getPublicType()){
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
                intent = new Intent(getApplication(), PartyActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

}
