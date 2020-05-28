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
import android.widget.Toast;

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

        /*for (User x : response.body()) {
            if (x.getRemember()){
                startActivity(intent);
            }*/

        Call<List<User>> call3 = service.getUsers();

        call3.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    Log.d("User autoLo not success", "Code: " + response.code());
                    return;
                }
                for (User x : response.body()) {
                    if (x.getRemember()) {
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d("user AutoLo error", "Code: " + t.toString());
            }
        });

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
                                    if (u.getUsername().equals(username.getText().toString()) && u.getPassword().equals(password.getText().toString())) {
                                        u.setRemember(true);
                                        Call<User> call2 = service.patchUser(u.getId(), u);
                                        call2.enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                if(!response.isSuccessful()){
                                                    Log.d("User patch not success", "Code: " + response.code());
                                                    return;
                                                }
                                                startActivity(intent);
                                                Toast.makeText(LoginActivity.this, "user will be remembered", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {
                                                Log.d("user patch error", "Code: " + t.toString());
                                            }
                                        });
                                    } else
                                        errorView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        //username.getText();
                        //password.getText();

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Log.d("goin_onFailure","true");
                        Log.d("eccezione",t+"");
                    }
                });
            }
        });



    }
}
