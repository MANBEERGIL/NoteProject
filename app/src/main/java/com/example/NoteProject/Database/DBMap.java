package com.example.NoteProject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.NoteProject.Modals.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBMap {

    public static final String TABLE_MAP = "tblmap";
    public static final String MAP_ID = "mapId";
    public static final List<String> MAP_LOCATION = Collections.singletonList("Location");
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String NOTE_ID = "noteId";


    private Context context;
    private DBHelper dbHelper;

    public DBMap(Context context) {
        this.context = context;
    }



    public void insertMap(ContentValues contentValues){
        dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.insert(TABLE_MAP, null, contentValues);
        database.close();

    }

    public ArrayList<Map> getAllLocations(Context context, String noteId) {


        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database. rawQuery("SELECT * FROM " + TABLE_MAP + " WHERE "  +NOTE_ID + " =? ", new String[] {noteId});


        ArrayList<Map> mapArrayList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Map map = new Map();
                    map.setLat(cursor.getString(1));
                    map.setLng(cursor.getString(2));
                    map.setNoteId(cursor.getInt(3));
                   // image1.setImageLocation(cursor.getString(1));
                    //image1.setNoteId(cursor.getInt(2));
                    mapArrayList.add(map);
                }
            }
        }
        database.close();
        return mapArrayList;
    }
}
