package com.example.personalschemaapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseClass extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RestaurantDB";
    // Öka till 2 för att trigga onUpgrade och skapa nya kolumner
    private static final int DATABASE_VERSION = 2;

    public DatabaseClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ny tabellstruktur:
        //  - Local_id: lokalt autoincrement
        //  - Server_id: ID från backend
        //  - full_name, date, time, shift: övrig info
        String createTableQuery = "CREATE TABLE personal (" +
                "Local_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Server_id INTEGER, " +  // <-- ny kolumn för serverns ID
                "full_name TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "shift TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Om tabellen redan finns, ta bort och kör onCreate() igen med ny struktur
        db.execSQL("DROP TABLE IF EXISTS personal");
        onCreate(db);
    }

    /**
     * Lägger till eller uppdaterar ett schema baserat på (serverId, date, time).
     * Om en rad redan finns för den kombinationen, uppdateras den. Annars skapas ny.
     */
    public String addRecord(long serverId, String f_name, String l_name,
                            String date, String time, String shift) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Server_id", serverId); // Sätt server-ID från backend
        cv.put("full_name", f_name + " " + l_name);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("shift", shift);

        // Kontrollera om en rad redan finns för (serverId, date, time)
        try (Cursor cursor = db.query(
                "personal",
                new String[]{"Local_id"},
                "Server_id=? AND date=? AND time=?",
                new String[]{String.valueOf(serverId), date, time},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                // Uppdatera befintlig rad
                int res = db.update(
                        "personal",
                        cv,
                        "Server_id=? AND date=? AND time=?",
                        new String[]{String.valueOf(serverId), date, time}
                );
                return (res > 0) ? "Success" : "Failed";
            } else {
                // Infoga ny rad
                long res = db.insert("personal", null, cv);
                return (res != -1) ? "Success" : "Failed";
            }
        }
    }

    /**
     * Verifierar att en rad med visst serverId finns i lokala databasen.
     * (Om du använder lokala ID i stället, justera logiken.)
     */
    public boolean verifyEmployeeId(long serverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(
                "personal",
                new String[]{"Server_id"},
                "Server_id=?",
                new String[]{String.valueOf(serverId)},
                null, null, null)) {
            return cursor.moveToFirst(); // true om vi hittade minst en rad
        }
    }

    /**
     * Hämtar schemat för den inloggade användaren (baserat på serverId).
     */
    public Cursor mySchedule(long serverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT full_name, date, time, shift FROM personal WHERE Server_id = ?",
                new String[]{String.valueOf(serverId)}
        );
    }

    /**
     * Hämtar schemat för alla andra (där Server_id != serverId).
     */
    public Cursor otherShifts(long serverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT full_name, date, time, shift FROM personal WHERE Server_id != ?",
                new String[]{String.valueOf(serverId)}
        );
    }

    /**
     * Hämtar alla scheman för ett visst datum (oavsett vem).
     */
    public Cursor getShiftsForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                "personal",
                new String[]{"full_name", "date", "time", "shift"},
                "date=?", new String[]{date},
                null, null, null
        );
    }
}
