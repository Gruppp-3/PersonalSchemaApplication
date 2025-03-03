package com.example.personalschemaapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    TextInputEditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }


    public void signIn(View view) {
        String enteredUsername = username.getText().toString().trim();
        String enteredPassword = password.getText().toString().trim();


        String correctUsername = "admin";
        String correctPassword = "password123";

        if (enteredUsername.equals(correctUsername) && enteredPassword.equals(correctPassword)) {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Felaktigt användarnamn eller lösenord", Toast.LENGTH_SHORT).show();
        }
    }

}
