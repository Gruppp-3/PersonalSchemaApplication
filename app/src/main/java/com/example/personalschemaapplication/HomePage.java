package com.example.personalschemaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalschemaapplication.WorkShiftAdapter;
import com.example.personalschemaapplication.api.ApiService;
import com.example.personalschemaapplication.api.RetrofitClient;
import com.example.personalschemaapplication.Employee;
import com.example.personalschemaapplication.WorkShift;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity implements WorkShiftAdapter.OnShiftActionListener {
    private static final String TAG = "HomePage";

    private RecyclerView recyclerViewSchedule;
    private RecyclerView recyclerViewOtherShifts;
    private WorkShiftAdapter myScheduleAdapter;
    private WorkShiftAdapter otherShiftsAdapter;
    private List<WorkShift> myShifts = new ArrayList<>();
    private List<WorkShift> otherShifts = new ArrayList<>();
    private TextView welcomeText;
    private Long currentEmployeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Get employee ID from intent
        String employeeIdStr = getIntent().getStringExtra("employee_id");
        if (employeeIdStr == null || employeeIdStr.isEmpty()) {
            Toast.makeText(this, "No employee ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentEmployeeId = Long.parseLong(employeeIdStr);
        Log.d(TAG, "Employee ID: " + currentEmployeeId);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        recyclerViewOtherShifts = findViewById(R.id.recyclerViewOtherShifts);

        // Set up recycler views
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOtherShifts.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapters
        myScheduleAdapter = new WorkShiftAdapter(myShifts, this, this);
        otherShiftsAdapter = new WorkShiftAdapter(otherShifts, this, this);

        // Set to employee view mode (hide delete buttons, etc.)
        myScheduleAdapter.setEmployeeView(true);
        otherShiftsAdapter.setEmployeeView(true);

        // Set adapters to RecyclerViews
        recyclerViewSchedule.setAdapter(myScheduleAdapter);
        recyclerViewOtherShifts.setAdapter(otherShiftsAdapter);

        // Load data
        loadEmployeeInfo();
        loadMySchedule();
        loadOtherShifts();

        // Setup bottom navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    return true;
                } else if (itemId == R.id.navigation_calender) {
                    Intent intent = new Intent(HomePage.this, Calender.class);
                    intent.putExtra("employee_id", employeeIdStr);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadEmployeeInfo() {
        Log.d(TAG, "Loading employee info for ID: " + currentEmployeeId);
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<Employee> call = apiService.getEmployeeById(currentEmployeeId);

        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                Log.d(TAG, "Employee info response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Employee employee = response.body();
                    String greeting = "Välkommen " + employee.getFirstName() + "!";
                    welcomeText.setText(greeting);
                } else {
                    Log.e(TAG, "Failed to get employee info: " + response.message());
                    welcomeText.setText("Välkommen Anställd #" + currentEmployeeId);
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Log.e(TAG, "Failed to load employee info", t);
                welcomeText.setText("Välkommen Anställd #" + currentEmployeeId);
            }
        });
    }

    private void loadMySchedule() {
        Log.d(TAG, "Loading schedule for employee ID: " + currentEmployeeId);
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<List<WorkShift>> call = apiService.getWorkShiftsByEmployee(currentEmployeeId);

        call.enqueue(new Callback<List<WorkShift>>() {
            @Override
            public void onResponse(Call<List<WorkShift>> call, Response<List<WorkShift>> response) {
                Log.d(TAG, "Schedule response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    myShifts.clear();
                    myShifts.addAll(response.body());
                    myScheduleAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + myShifts.size() + " shifts for employee ID " + currentEmployeeId);
                } else {
                    Log.e(TAG, "Failed to load schedule: " + response.message());
                    Toast.makeText(HomePage.this, "Kunde inte ladda ditt schema", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WorkShift>> call, Throwable t) {
                Log.e(TAG, "Network error loading schedule", t);
                Toast.makeText(HomePage.this, "Nätverksfel vid laddning av schema", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOtherShifts() {
        Log.d(TAG, "Loading other and unassigned shifts");
        ApiService apiService = RetrofitClient.getInstance().getApi();

        // First, get unassigned shifts
        Call<List<WorkShift>> unassignedCall = apiService.getUnassignedWorkShifts();
        unassignedCall.enqueue(new Callback<List<WorkShift>>() {
            @Override
            public void onResponse(Call<List<WorkShift>> call, Response<List<WorkShift>> response) {
                Log.d(TAG, "Unassigned shifts response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    otherShifts.clear();
                    otherShifts.addAll(response.body());
                    otherShiftsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Loaded " + otherShifts.size() + " unassigned shifts");

                    // Now load other employees' shifts
                    loadShiftsOfOtherEmployees();
                } else {
                    Log.e(TAG, "Failed to load unassigned shifts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<WorkShift>> call, Throwable t) {
                Log.e(TAG, "Network error loading unassigned shifts", t);
                Toast.makeText(HomePage.this, "Kunde inte ladda lediga arbetspass", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadShiftsOfOtherEmployees() {
        Log.d(TAG, "Loading shifts of other employees");
        ApiService apiService = RetrofitClient.getInstance().getApi();
        // Get all shifts
        Call<List<WorkShift>> allShiftsCall = apiService.getAllWorkShifts();
        allShiftsCall.enqueue(new Callback<List<WorkShift>>() {
            @Override
            public void onResponse(Call<List<WorkShift>> call, Response<List<WorkShift>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkShift> allShifts = response.body();
                    List<WorkShift> otherEmployeeShifts = new ArrayList<>();

                    // Filter out shifts that belong to the current employee and unassigned shifts
                    for (WorkShift shift : allShifts) {
                        if (shift.getEmployee() != null &&
                                !shift.getEmployee().getId().equals(currentEmployeeId)) {
                            // This shift belongs to another employee
                            otherEmployeeShifts.add(shift);
                        }
                    }

                    // Add other employees' shifts to the list
                    otherShifts.addAll(otherEmployeeShifts);
                    otherShiftsAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Added " + otherEmployeeShifts.size() + " shifts from other employees");
                } else {
                    Log.e(TAG, "Failed to load all shifts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<WorkShift>> call, Throwable t) {
                Log.e(TAG, "Network error loading all shifts", t);
            }
        });
    }

    @Override
    public void onDeleteShift(WorkShift workShift) {
        // Not implemented for employee view
        Toast.makeText(this, "Ta bort arbetspass är endast tillgängligt för chefer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAssignEmployee(WorkShift workShift) {
        if (workShift.getEmployee() == null) {
            // This is an unassigned shift - show dialog to take it
            showTakeShiftDialog(workShift);
        } else if (workShift.getEmployee().getId().equals(currentEmployeeId)) {
            // This is the current employee's shift - we can't swap with ourselves
            Toast.makeText(this, "Detta är redan ditt arbetspass", Toast.LENGTH_SHORT).show();
        } else {
            // This is another employee's shift - currently not implemented
            Toast.makeText(this, "Bytesfunktion är ännu inte implementerad", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTakeShiftDialog(WorkShift workShift) {
        new AlertDialog.Builder(this)
                .setTitle("Ta arbetspass")
                .setMessage("Vill du ta detta lediga arbetspass?")
                .setPositiveButton("Ja", (dialog, which) -> {
                    // Call API to take this shift
                    takeShift(workShift);
                })
                .setNegativeButton("Nej", null)
                .show();
    }

    private void takeShift(WorkShift workShift) {
        ApiService apiService = RetrofitClient.getInstance().getApi();
        Call<WorkShift> call = apiService.assignShiftToEmployee(workShift.getId(), currentEmployeeId);

        call.enqueue(new Callback<WorkShift>() {
            @Override
            public void onResponse(Call<WorkShift> call, Response<WorkShift> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(HomePage.this, "Arbetspasset är nu tilldelat till dig", Toast.LENGTH_SHORT).show();

                    // Reload both lists to show the updated assignments
                    loadMySchedule();
                    loadOtherShifts();
                } else {
                    Toast.makeText(HomePage.this, "Kunde inte tilldela arbetspasset", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkShift> call, Throwable t) {
                Log.e(TAG, "Failed to take shift", t);
                Toast.makeText(HomePage.this, "Nätverksfel vid tilldelning av arbetspass", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when coming back to this activity
        loadMySchedule();
        loadOtherShifts();
    }
}