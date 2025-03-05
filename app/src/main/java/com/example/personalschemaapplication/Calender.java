package com.example.personalschemaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.CalendarView;
import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class Calender extends AppCompatActivity {

    CalendarView calendarView;
    String selectedDate;
    RecyclerView recyclerView;
    DatabaseClass dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dh = new DatabaseClass(this);


        long currentDate = System.currentTimeMillis();
        calendarView.setDate(currentDate, false, true);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                updateRecyclerViewForDate(selectedDate);
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.navigation_home) {
                    startActivity(new Intent(Calender.this, HomePage.class));
                    return true;
                } else if(itemId == R.id.navigation_calender) {
                    // Redan i denna aktivitet
                    return true;
                }
                return false;
            }
        });
    }

    private void updateRecyclerViewForDate(String date) {
        List<String> detailsForSelectedDate = new ArrayList<>();

        boolean shift1Filled = false;
        boolean shift2Filled = false;

        Cursor cursor = dh.getShiftsForDate(date);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("full_name"));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
            detailsForSelectedDate.add(name + "  " + time);

            if ("11-17".equals(time)) {
                shift1Filled = true;
            } else if ("17-21".equals(time)) {
                shift2Filled = true;
            }
        }
        cursor.close();

        if (!shift1Filled) {
            detailsForSelectedDate.add("Empty  11-17");
        }
        if (!shift2Filled) {
            detailsForSelectedDate.add("Empty  17-21");
        }

        SimpleStringAdapter adapter = new SimpleStringAdapter(detailsForSelectedDate);
        recyclerView.setAdapter(adapter);
    }

    public static class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.ViewHolder> {
        private List<String> data;

        public SimpleStringAdapter(List<String> data) {
            this.data = data;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
            holder.itemView.setOnClickListener(v -> {
                String selectedItem = data.get(position);
                String[] parts = selectedItem.split("  ");
                if(parts.length >= 2){
                    String name = parts[0];
                    String time = parts[1];
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Shift Details")
                            .setMessage("Name: " + name + "\nTime: " + time)
                            .setPositiveButton("Change Pass", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Exempel: välj tid baserat på positionen
                                    String selectedTime = position == 0 ? "11-17" : "17-21";
                                    new DatabaseClass(v.getContext())
                                            .addRecord("Omran", "Suleiman", ((Calender)v.getContext()).selectedDate, selectedTime, "True");
                                    Toast.makeText(v.getContext(), "Result: " + "result", Toast.LENGTH_SHORT).show();
                                    if(v.getContext() instanceof Calender){
                                        ((Calender)v.getContext()).updateRecyclerViewForDate(((Calender)v.getContext()).selectedDate);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
