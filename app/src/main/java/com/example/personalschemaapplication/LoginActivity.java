package com.example.personalschemaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.personalschemaapplication.api.ApiService;
import com.example.personalschemaapplication.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextId;
    private Button buttonLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiera Retrofit API
        apiService = RetrofitClient.getInstance().getApi();

        // Initiera UI-komponenter
        editTextId = findViewById(R.id.editTextId);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String idInput = editTextId.getText().toString().trim();

            if (!idInput.isEmpty()) {
                // Anropa API-metoden för att verifiera ID
                apiService.verifyEmployeeId(idInput).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean exists = response.body();
                            if (exists) {
                                // Inloggning lyckades
                                Toast.makeText(LoginActivity.this, "Inloggning lyckades", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomePage.class);
                                intent.putExtra("employee_id", idInput);
                                startActivity(intent);
                                finish();
                            } else {
                                // Ogiltigt ID
                                Toast.makeText(LoginActivity.this, "Ogiltigt ID", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Serverproblem eller ogiltigt svar
                            Toast.makeText(LoginActivity.this, "Fel vid inloggning, försök igen.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        // Fel vid nätverksanrop
                        Log.e("LoginActivity", "Nätverksfel", t);
                        Toast.makeText(LoginActivity.this, "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                editTextId.setError("Ange ditt ID");
            }
        });
    }
}
