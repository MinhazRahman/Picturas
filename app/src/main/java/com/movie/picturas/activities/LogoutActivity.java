package com.movie.picturas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.movie.picturas.R;
import com.parse.ParseUser;

import java.util.Objects;

public class LogoutActivity extends AppCompatActivity {

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // find views in the layout
        btnLogout = findViewById(R.id.btnLogout);

        // set click listener on log out button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log out the user
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

                Toast.makeText(LogoutActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();

                // Navigate to login activity
                Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}