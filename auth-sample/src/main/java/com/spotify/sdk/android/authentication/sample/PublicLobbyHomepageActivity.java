package com.spotify.sdk.android.authentication.sample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class PublicLobbyHomepageActivity extends AppCompatActivity {
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_lobby_homepage);

        textViewResult = findViewById(R.id.text_view_result);

        PublicLobbyHomepageService retrofit = RetrofitInstance.getRetrofitInstance().create(PublicLobbyHomepageService.class);

        Call<List<Lobby>> call = retrofit.getLobbys();

        call.enqueue(new Callback<List<Lobby>>() {
            @Override
            public void onResponse(Call<List<Lobby>> call, Response<List<Lobby>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                List<Lobby> lobbys = response.body();
                Collections.sort(lobbys);
                for(Lobby lobby : lobbys){
                    if(lobby.getPublicType()){
                        String content = "";
                        content += "LobbyID: " + lobby.getId() + "\n";
                        content += "name: " + lobby.getName() + "\n";
                        if(lobby.getGenre() != null){content += "genre: " + lobby.getGenre() + "\n";}
                        if(lobby.getMood() != null){content += "mood: " + lobby.getMood() + "\n";}
                        content += "partecipantNumber: " + lobby.getPartecipantNumber() + "\n";
                        content += "\n";
                        textViewResult.append(content);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Lobby>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });


    }

}
