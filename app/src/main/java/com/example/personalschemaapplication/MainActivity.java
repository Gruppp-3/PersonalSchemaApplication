package com.example.personalschemaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalschemaapplication.api.ApiService;
import com.example.personalschemaapplication.api.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText editTextId;
    private Button buttonLogin;
    private TextView welcomeMessage, footer;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hämta referenser till UI-komponenterna
        logo = findViewById(R.id.logo);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        editTextId = findViewById(R.id.editTextId);
        buttonLogin = findViewById(R.id.buttonLogin);
        footer = findViewById(R.id.footer);

        // Sätt klicklyssnare på inloggningsknappen
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeIdStr = editTextId.getText().toString().trim();

                if (!employeeIdStr.isEmpty()) {
                    try {
                        // Convert String to Long before calling API
                        Long employeeId = Long.parseLong(employeeIdStr);
                        verifyEmployeeId(employeeId, employeeIdStr);
                    } catch (NumberFormatException e) {
                        editTextId.setError("Ange ett giltigt numeriskt ID");
                    }
                } else {
                    editTextId.setError("Ange ditt ID");
                }
            }
        });
    }

    // Funktion för att verifiera anställd ID
    private void verifyEmployeeId(Long employeeId, String employeeIdStr) {
        ApiService apiService = RetrofitClient.getInstance().getApi();

        // Using verifyEmployeeId which should return a boolean
        Call<Boolean> call = apiService.verifyEmployeeId(employeeId);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    // Om ID är giltigt, gå vidare till HomePage
                    Toast.makeText(MainActivity.this, "Inloggning lyckades", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtra("employee_id", employeeIdStr);
                    startActivity(intent);
                    finish();  // Stäng MainActivity så användaren inte kan gå tillbaka
                } else {
                    // Om ID inte finns i databasen
                    Toast.makeText(MainActivity.this, "Ogiltigt ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Om nätverksanropet misslyckades
                Toast.makeText(MainActivity.this, "Nätverksfel. Försök igen senare.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}