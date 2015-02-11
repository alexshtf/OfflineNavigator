package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MapsDbOpenHelper extends SQLiteOpenHelper {
    private static final int CURRENT_VERSION = 1;
    private static final String DB_NAME = "Maps";
    private static final SQLiteDatabase.CursorFactory DEFAULT_CURSOR_FACTORY = null;

    public MapsDbOpenHelper(Context context) {
        super(context, DB_NAME, DEFAULT_CURSOR_FACTORY, CURRENT_VERSION);
    }

    public static MapsDbOpenHelper from(Context context) {
        return new MapsDbOpenHelper(context);
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


}
