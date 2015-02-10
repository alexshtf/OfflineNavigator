package com.alexshtf.offlinenavigator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.alexshtf.offlinenavigator.Utils.arrayOf;

public class MapsDb extends SQLiteOpenHelper {

    public static final String MAP_ID = "_id";
    public static final String MAP_NAME = "name";
    public static final String MAP_IMAGE_URL = "image_url";

    public static final String MAPS_TABLE = "maps";

    private static final int CURRENT_VERSION = 1;
    private static final String DB_NAME = "Maps";
    private static final SQLiteDatabase.CursorFactory DEFAULT_CURSOR_FACTORY = null;

    public MapsDb(Context context) {
        super(context, DB_NAME, DEFAULT_CURSOR_FACTORY, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(
                    "CREATE TABLE maps(" +
                            "_id INTEGER PRIMARY KEY, " +
                            "name TEXT NOT NULL, " +
                            "image_url TEXT NOT NULL" +
                            ")");
            db.execSQL(
                    "CREATE TABLE anchors(" +
                            "image_x REAL, " +
                            "image_y REAL, " +
                            "map_long REAL, " +
                            "map_lat REAL, " +
                            "map_id INTEGER, " +
                            "PRIMARY KEY (image_x, image_y), " +
                            "FOREIGN KEY (map_id) REFERENCES maps(id)" +
                            ")");
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing. We currently don't have any previous database versions.
    }

    public Cursor getMaps() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                MAPS_TABLE, arrayOf(MAP_ID, MAP_NAME, MAP_IMAGE_URL),
                null, null, null, null, null
        );
    }


    public long addMap(String name, String imageUrl) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(MAP_NAME, name);
            values.put(MAP_IMAGE_URL, imageUrl);
            return db.insert(MAPS_TABLE, null, values);
        }
        finally {
            db.close();
        }
    }
}
