package com.spotify.sdk.android.authentication.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

<<<<<<< HEAD
/*        Button loginButton = findViewById(R.id.loginButton);
=======
        Button loginButton = findViewById(R.id.loginButton);
>>>>>>> 52949f8b55c6cb2d8776d3efe08c793e3b4b907f

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
<<<<<<< HEAD
        });*/
=======
        });
>>>>>>> 52949f8b55c6cb2d8776d3efe08c793e3b4b907f
    }
}
