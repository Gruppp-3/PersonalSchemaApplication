package com.example.personalschemaapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseClass extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RestaurantDB";
    private static final int DATABASE_VERSION = 1;

    public DatabaseClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE personal (" +
                "Employee_nr INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "shift TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS personal");
        onCreate(db);
    }

    // Lägger till eller uppdaterar ett schema baserat på datum och tid
    public String addRecord(String f_name, String l_name, String date, String time, String shift) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("full_name", f_name + " " + l_name);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("shift", shift);

        // Kontrollera om ett schema redan finns för samma datum och tid
        try (Cursor cursor = db.query("personal", new String[]{"full_name"}, "date=? AND time=?", new String[]{date, time}, null, null, null)) {
            if (cursor.moveToFirst()) {
                int res = db.update("personal", cv, "date=? AND time=?", new String[]{date, time});
                return res > 0 ? "Success" : "Failed";
            } else {
                long res = db.insert("personal", null, cv);
                return res != -1 ? "Success" : "Failed";
            }
        }
    }

    // Verifierar att ett givet Employee_nr (ID) finns i databasen
    public boolean verifyEmployeeId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query("personal", new String[]{"Employee_nr"}, "Employee_nr=?", new String[]{id}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    // Hämtar schemat för den inloggade användaren baserat på Employee_nr
    public Cursor mySchedule(String employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT full_name, date, time FROM personal WHERE Employee_nr = ?", new String[]{employeeId});
    }

    // Hämtar andras scheman, dvs de vars Employee_nr inte matchar den inloggade användarens ID
    public Cursor otherShifts(String employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT full_name, date, time FROM personal WHERE Employee_nr != ?", new String[]{employeeId});
    }

    // Hämtar alla scheman för ett visst datum
    public Cursor getShiftsForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("personal", new String[]{"full_name", "date", "time"}, "date=?", new String[]{date}, null, null, null);
    }
}
