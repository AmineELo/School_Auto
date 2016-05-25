package com.example.ayoub.school_auto;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AmineElouattar on 4/5/16.
 */
public class Database_Manager extends SQLiteOpenHelper{

    public Database_Manager(Context context){
        super(context, "DB1", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS location (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lat text, lng text, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addElement(String lat, String lng){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("lat", lat);
        values.put("lng", lng);
        values.put("timestamp", "CURRENT_TIMESTAMP");

        db.insert("location", null, values);
        db.close();
    }
}
