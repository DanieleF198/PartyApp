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

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        LoginService service = RetrofitInstance.getRetrofitInstance().create(LoginService.class);
        Log.d("PACKAGE_NAME", getApplicationContext().getPackageName()+"");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*  username.getText();
                password.getText();*/

              Log.d("LOGINBUTTON_CLICKED","true");
                Call<User> call = service.getUsers();

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d("goin_onResponse","true");
                        Log.d("response.body()", response.body()+"");
                        Log.d("response.code()", response.code()+"");
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("goin_onFailure","true");
                        Log.d("eccezione",t+"");

                    }
                });

            }
        });



    }
}
