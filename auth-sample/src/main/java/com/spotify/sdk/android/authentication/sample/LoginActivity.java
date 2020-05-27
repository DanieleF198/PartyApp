package com.spotify.sdk.android.authentication.sample;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        TextView errorView = findViewById(R.id.errorView);


        LoginService service = RetrofitInstance.getRetrofitInstance().create(LoginService.class);
        Log.d("PACKAGE_NAME", getApplicationContext().getPackageName()+"");
        Intent intent = new Intent(this, MainActivity.class);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              Log.d("LOGINBUTTON_CLICKED","true");
              Call<List<User>> call = service.getUsers();

                call.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        Log.d("goin_onResponse","true");
                        if(response.body()!=null) {
                            for (User u : response.body()) {
                                if(u.getUsername().equals(username.getText().toString()) && u.getPassword().equals(password.getText().toString()))
                                    startActivity(intent);
                                else
                                    errorView.setVisibility(View.VISIBLE);
                            }
                        }
                        //username.getText();
                        //password.getText();

                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Log.d("goin_onFailure","true");
                        Log.d("eccezione",t+"");
                    }
                });
            }
        });

        Button gotoListsButton = findViewById(R.id.gotoLists);

        gotoListsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,PublicLobbyHomepageActivity.class);
                view.getContext().startActivity(intent);
            }
        });

    }
}
