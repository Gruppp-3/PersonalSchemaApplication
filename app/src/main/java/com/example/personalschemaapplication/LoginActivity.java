package com.example.personalschemaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import com.example.personalschemaapplication.api.ApiService;
import com.example.personalschemaapplication.api.RetrofitClient;
import com.example.personalschemaapplication.model.EmployeeAvailability;
import java.util.ArrayList;
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
                long employeeId;
                try {
                    employeeId = Long.parseLong(idInput);  // Försök omvandla sträng till long
                } catch (NumberFormatException e) {
                    // Ogiltig siffra
                    editTextId.setError("Ange ett giltigt nummer");
                    return;
                }

                // Anropa API-metod med long
                apiService.verifyEmployeeId(employeeId).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean exists = response.body();
                            if (exists) {
                                // Inloggning lyckades
                                Toast.makeText(LoginActivity.this, "Inloggning lyckades", Toast.LENGTH_SHORT).show();

                                // Hämta schema för den inloggade anställda
                                apiService.getEmployeeAvailability(employeeId).enqueue(new Callback<List<EmployeeAvailability>>() {
                                    @Override
                                    public void onResponse(Call<List<EmployeeAvailability>> call,
                                                           Response<List<EmployeeAvailability>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            List<EmployeeAvailability> availabilityList = response.body();
                                            Log.d("LoginActivity", "Schema hämtat: " + availabilityList.size() + " poster");

                                            Toast.makeText(LoginActivity.this, "Schema hämtat", Toast.LENGTH_SHORT).show();

                                            // Starta annan aktivitet
                                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                                            // Skicka vidare employeeId och listan om du vill
                                            intent.putExtra("employee_id", employeeId);
                                            intent.putExtra("availability_list", new ArrayList<>(availabilityList));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.e("LoginActivity", "Fel vid API-svar: " + response.message());
                                            Toast.makeText(LoginActivity.this, "Fel vid hämtning av schema: " + response.message(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<EmployeeAvailability>> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this, "Nätverksfel: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
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
