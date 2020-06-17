package com.spotify.sdk.android.authentication.sample.activity;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.ws.retrofit.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.model.User;
import com.spotify.sdk.android.authentication.sample.ws.model.UserRemember;
import com.spotify.sdk.android.authentication.sample.ws.service.UserService;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        TextView errorView = findViewById(R.id.errorView);

        boolean fileExists = false;
        String defaultJson = "{\"id\":\"\",\"username\":\"\",\"password\":\"\",\"remember\":false}";
        gson = new Gson();
        Intent intent = new Intent(this, MainActivity.class);


        String[] files = getApplicationContext().fileList();

        for (String s : files) {
            if (s.equals("remember.json")) {
                //non crearlo e puoi scriverci
                fileExists = true;

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
                    Log.d("userRememberDEBUG", userRemember.getUsername() + " " + userRemember.getPassword() + " " + userRemember.isRemember() + " ");
                    if (userRemember.isRemember())
                        startAct(intent);
                }

            }
        }


        if (!fileExists)
            try (FileOutputStream fos = getApplicationContext().openFileOutput("remember.json", Context.MODE_PRIVATE)) {
                fos.write(defaultJson.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }


        UserService service = RetrofitInstance.getRetrofitInstance().create(UserService.class);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("LOGINBUTTON_CLICKED", "true");
                Call<List<User>> call = service.getUsers();

                call.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                        Log.d("goin_onResponse", "true");
                        Log.d("DEBUG_RESPONSE", response.body() + "");
                        if (response.body() != null) {


                            for (User u : response.body()) {
                                if (u.getUsername().equals(username.getText().toString()) && u.getPassword().equals(password.getText().toString())) {


                                    UserRemember userRemember = new UserRemember(u.getId(), username.getText().toString(), password.getText().toString(), true);
                                    String json = gson.toJson(userRemember);
                                    try (FileOutputStream fos = getApplicationContext().openFileOutput("remember.json", Context.MODE_PRIVATE)) {
                                        fos.write(json.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    startActivity(intent);
                                    return;

                                }
                            }
                            errorView.setVisibility(View.VISIBLE);
                        }

                    }


                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Log.d("goin_onFailure", "true");
                        Log.d("eccezione", t + "");
                    }
                });
            }
        });
    }


    private void startAct(Intent intent){
        startActivity(intent);
    }

}
