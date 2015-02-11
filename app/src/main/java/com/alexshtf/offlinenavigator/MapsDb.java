package com.alexshtf.offlinenavigator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.alexshtf.offlinenavigator.Utils.arrayOf;
import static com.alexshtf.offlinenavigator.Utils.stringArrayOf;

public class MapsDb {
    public static final String MAP_ID = "_id";
    public static final String MAP_NAME = "name";
    public static final String MAP_IMAGE_URI = "image_url";
    public static final String MAPS_TABLE = "maps";

    public static Cursor getAllMaps(SQLiteDatabase db) {
        return db.query(
                MAPS_TABLE, arrayOf(MAP_ID, MAP_NAME, MAP_IMAGE_URI),
                null, null, null, null, null
        );
    }

    public static MapInfo getMapInfo(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(MAP_NAME));
        String imageUrl = cursor.getString(cursor.getColumnIndex(MAP_IMAGE_URI));

        return new MapInfo(name, imageUrl);
    }

    public static MapInfo getMap(SQLiteDatabase db, long mapId) {
        Cursor cursor = db.query(
                MAPS_TABLE, arrayOf(MAP_NAME, MAP_IMAGE_URI),
                "_id == ?", stringArrayOf(mapId),
                null, null, null
        );

        try {
            if (cursor.moveToNext())
                return getMapInfo(cursor);
            else
                throw new RuntimeException("Did not find map with the specified ID");
        }
        finally {
            cursor.close();
        }
    }

    public static long addMap(MapsDbOpenHelper mapsDb, String name, String imageUri) {
        SQLiteDatabase db = mapsDb.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(MAP_NAME, name);
            values.put(MAP_IMAGE_URI, imageUri);
            return db.insert(MAPS_TABLE, null, values);
        }
        finally {
            db.close();
        }
    }

    public static class MapInfo {
        private final String name;
        private final String imageUri;

        public MapInfo(String name, String imageUri) {
            this.name = name;
            this.imageUri = imageUri;
        }

        public String getName() {
            return name;
        }

        public String getImageUri() {
            return imageUri;
        }
    }
}
