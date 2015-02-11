package com.alexshtf.offlinenavigator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexshtf.interp.Point;

import static com.alexshtf.interp.Point.xy;
import static com.alexshtf.offlinenavigator.Utils.arrayOf;
import static com.alexshtf.offlinenavigator.Utils.stringArrayOf;
import static java.lang.String.format;

public class MapsDb {
    public static final String MAPS_TABLE = "maps";
    public static final String ANCHORS_TABLE = "anchors";

    public static final String MAP_ID = "_id";
    public static final String MAP_NAME = "name";
    public static final String MAP_IMAGE_URI = "image_url";

    public static final String ANCHOR_IMAGE_X = "image_x";
    public static final String ANCHOR_IMAGE_Y = "image_y";
    public static final String ANCHOR_MAP_LONG = "map_long";
    public static final String ANCHOR_MAP_LAT = "map_lat";
    public static final String ANCHOR_MAP_ID = "map_id";

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
        ContentValues values = new ContentValues();
        values.put(MAP_NAME, name);
        values.put(MAP_IMAGE_URI, imageUri);
        SQLiteDatabase db = mapsDb.getWritableDatabase();
        return db.insert(MAPS_TABLE, null, values);
    }

    public static AnchorInfo[] getAnchors(SQLiteDatabase db, long mapId) {
        Cursor cursor = db.query(
                ANCHORS_TABLE, arrayOf(ANCHOR_IMAGE_X, ANCHOR_IMAGE_Y, ANCHOR_MAP_LONG, ANCHOR_MAP_LAT),
                "map_id == ?", stringArrayOf(mapId),
                null, null, null
        );

        try {
            int idxImageX = cursor.getColumnIndex(ANCHOR_IMAGE_X);
            int idxImageY = cursor.getColumnIndex(ANCHOR_IMAGE_Y);
            int idxMapLong = cursor.getColumnIndex(ANCHOR_MAP_LONG);
            int idxMapLat = cursor.getColumnIndex(ANCHOR_MAP_LAT);

            AnchorInfo[] result = new AnchorInfo[cursor.getCount()];
            for(int i = 0; cursor.moveToNext(); ++i)
                result[i] = new AnchorInfo(
                        xy(cursor.getFloat(idxImageX), cursor.getFloat(idxImageY)),
                        xy(cursor.getFloat(idxMapLong), cursor.getFloat(idxMapLat))
                );

            return result;
        }
        finally {
            cursor.close();
        }
    }

    public static void addAnchor(MapsDbOpenHelper mapsDbOpenHelper, long mapId, AnchorInfo anchorInfo) {
        ContentValues values = new ContentValues();

        values.put(ANCHOR_MAP_ID, mapId);
        values.put(ANCHOR_IMAGE_X, anchorInfo.getPoinOnImage().getX());
        values.put(ANCHOR_IMAGE_Y, anchorInfo.getPoinOnImage().getY());
        values.put(ANCHOR_MAP_LONG, anchorInfo.getPointOnMap().getX());
        values.put(ANCHOR_MAP_LAT, anchorInfo.getPointOnMap().getY());

        SQLiteDatabase db = mapsDbOpenHelper.getWritableDatabase();
        db.insert(ANCHORS_TABLE, null, values);
    }

    public static void removeAnchor(MapsDbOpenHelper mapsDbOpenHelper, long mapId, Point point) {
        SQLiteDatabase db = mapsDbOpenHelper.getWritableDatabase();
        db.delete(ANCHORS_TABLE,
                format("%s == ? AND %s == ? AND %s == ?", ANCHOR_MAP_ID, ANCHOR_IMAGE_X, ANCHOR_IMAGE_Y),
                stringArrayOf(mapId, point.getX(), point.getY())
        );
    }

    public static void deleteMap(MapsDbOpenHelper mapsDb, long mapId) {
        SQLiteDatabase db = mapsDb.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(MAPS_TABLE, format("%s == ?", MAP_ID), stringArrayOf(mapId));
            db.delete(ANCHORS_TABLE, format("%s == ?", ANCHOR_MAP_ID), stringArrayOf(mapId));
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }
}
