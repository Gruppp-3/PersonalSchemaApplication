package com.example.personalschemaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalschemaapplication.api.ApiService;
import com.example.personalschemaapplication.api.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {

    private TextView scheduleTextView;
    private String employeeId;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Hämta anställd ID från Intent
        employeeId = getIntent().getStringExtra("employee_id");

        // Initiera TextView för att visa schemat
        scheduleTextView = findViewById(R.id.scheduleTextView);

        // Initiera BottomNavigationView
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Hämta schemat för användaren
        getEmployeeSchedule();
    }

    // Funktion för att hantera klickhändelser i BottomNavigationView
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            // Redan på HomePage, gör inget
            return true;
        } else if (itemId == R.id.navigation_calender) {
            // Navigera till Calender-aktiviteten
            Intent intent = new Intent(HomePage.this, Calender.class);
            intent.putExtra("employee_id", employeeId);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.navigation_logout) {
            // Navigera till Profile-aktiviteten
            Intent intent = new Intent(HomePage.this, LoginActivity.class);
            intent.putExtra("employee_id", employeeId);
            startActivity(intent);
            return true;
        }
        return false;
    }

    // Funktion för att hämta schemat
    private void getEmployeeSchedule() {
        // Konvertera employeeId (String) -> long
        long idLong;
        try {
            idLong = Long.parseLong(employeeId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ogiltigt ID (ej en siffra)", Toast.LENGTH_SHORT).show();
            return; // Avbryt om vi inte kan parsa
        }

        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<List<Map<String, Object>>> call = apiService.getEmployeeSchedule(idLong);

        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call,
                                   Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> schedule = response.body();
                    // Bearbeta schemat (ex. visa i scheduleTextView)
                    if (schedule.isEmpty()) {
                        Toast.makeText(HomePage.this, "Inget schema hittades.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Exempel på hur du kan skriva ut schemat i TextView
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, Object> shift : schedule) {
                            sb.append("Shift: ").append(shift.toString()).append("\n");
                        }
                        scheduleTextView.setText(sb.toString());
                    }
                } else {
                    Toast.makeText(HomePage.this, "Kunde inte hämta schema", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(HomePage.this, "Nätverksfel. Försök igen senare.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
