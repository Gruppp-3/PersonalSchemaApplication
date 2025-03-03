package com.example.personalschemaapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseClass extends SQLiteOpenHelper {

    private static final String dbname = "RestaurantDB";
    public DatabaseClass(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "CREATE TABLE personal (Employee_nr INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT, date TEXT, time TEXT, shift TEXT)";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS personal");
        onCreate(db);
    }

    public String addrecord(String f_name, String l_name, String date, String time, String shift) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("full_name", f_name + " " + l_name);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("shift", shift);

        Cursor cursor = db.query("personal", new String[] { "full_name" }, "date=? AND time=?", new String[] { date, time }, null, null, null);
        if (cursor.moveToFirst()) {
            int res = db.update("personal", cv, "date=? AND time=?", new String[] { date, time });
            cursor.close();
            return res > 0 ? "Success" : "Failed";
        } else {
            cursor.close();
            long res = db.insert("personal", null, cv);
            return res != -1 ? "Success" : "Failed";
        }
    }

    public Cursor MySchedule() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT full_name, date, time FROM personal WHERE full_name = 'Omran Suleiman'", null);
    }

    public Cursor OtherShifts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT full_name, date, time FROM personal WHERE full_name != 'Omran Suleiman'", null);
    }

    public Cursor getShiftsForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("personal", new String[] { "full_name", "date", "time" }, "date=?", new String[] { date }, null, null, null);
    }
}
