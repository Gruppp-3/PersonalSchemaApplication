package com.example.personalschemaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextId;
    private Button buttonLogin;
    private DatabaseClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseClass(this);
        editTextId = findViewById(R.id.editTextId);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String idInput = editTextId.getText().toString().trim();
            if (!idInput.isEmpty()) {
                boolean exists = db.verifyEmployeeId(idInput);
                Log.d("LoginActivity", "verifyEmployeeId returned: " + exists);
                if (exists) {
                    Intent intent = new Intent(LoginActivity.this, HomePage.class);
                    intent.putExtra("employee_id", idInput);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Ogiltigt ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Ange ditt ID", Toast.LENGTH_SHORT).show();
            }
        });




    }
}
