package com.example.personalschemaapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    RecyclerView recyclerViewSchedule, recyclerViewOtherShifts;
    DatabaseClass dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerViewSchedule = findViewById(R.id.recyclerViewSchedule);
        recyclerViewOtherShifts = findViewById(R.id.recyclerViewOtherShifts);

        // Sätt layout managers
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOtherShifts.setLayoutManager(new LinearLayoutManager(this));

        dh = new DatabaseClass(this);
        populateMySchedule();
        populateOtherShifts();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    return true;
                } else if (itemId == R.id.navigation_calender) {
                    startActivity(new Intent(HomePage.this, Calender.class));
                    return true;
                } else if (itemId == R.id.navigation_logout) {
                    startActivity(new Intent(HomePage.this, Login.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void populateMySchedule() {
        Cursor cursor = dh.MySchedule();
        List<String> scheduleList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String fullName = cursor.getString(cursor.getColumnIndex("full_name"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            scheduleList.add(fullName + ", " + date + " " + time);
        }
        cursor.close();

        SimpleStringAdapter adapter = new SimpleStringAdapter(scheduleList);
        recyclerViewSchedule.setAdapter(adapter);
    }

    private void populateOtherShifts() {
        Cursor cursor = dh.OtherShifts();
        List<String> scheduleList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String fullName = cursor.getString(cursor.getColumnIndex("full_name"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            scheduleList.add(fullName + ", " + date + " " + time);
        }
        cursor.close();

        SimpleStringAdapter adapter = new SimpleStringAdapter(scheduleList);
        recyclerViewOtherShifts.setAdapter(adapter);
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
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
