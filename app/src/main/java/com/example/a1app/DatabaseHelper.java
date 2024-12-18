package com.example.a1app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;

    // Tabla de Usuarios
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Tabla de Alarmas
    private static final String TABLE_ALARMS = "alarms";
    private static final String COLUMN_ALARM_ID = "id";
    private static final String COLUMN_ALARM_TIME = "time";
    private static final String COLUMN_ALARM_MESSAGE = "message";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Crear tabla de alarmas
        String createAlarmsTable = "CREATE TABLE " + TABLE_ALARMS + " (" +
                COLUMN_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ALARM_TIME + " TEXT, " +
                COLUMN_ALARM_MESSAGE + " TEXT)";
        db.execSQL(createAlarmsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }

    // ========================
    // Métodos para Usuarios
    // ========================
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Devuelve true si el registro fue exitoso
    }

    public String getPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_PASSWORD},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String password = cursor.getString(0);
            cursor.close();
            return password;
        }
        return null; // Usuario no encontrado
    }

    // ========================
    // Métodos para Alarmas
    // ========================
    // Insertar una nueva alarma
    public boolean insertAlarm(String time, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALARM_TIME, time);
        values.put(COLUMN_ALARM_MESSAGE, message);

        long result = db.insert(TABLE_ALARMS, null, values);
        db.close();
        return result != -1;
    }

    // Obtener todas las alarmas
    public ArrayList<Alarm> getAllAlarms() {
        ArrayList<Alarm> alarmList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARMS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALARM_ID));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALARM_TIME));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALARM_MESSAGE));
                alarmList.add(new Alarm(id, time, message));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return alarmList;
    }

    // Eliminar una alarma por ID
    public void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, COLUMN_ALARM_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
