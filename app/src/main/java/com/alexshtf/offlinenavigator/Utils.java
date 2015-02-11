package com.alexshtf.offlinenavigator;

import android.location.Location;

import com.alexshtf.interp.Point;

public class Utils {
    @SafeVarargs
    public static <T> T[] arrayOf(T... items) {
        return items;
    }

    public static String[] stringArrayOf(Object... items) {
        String[] result = new String[items.length];

        for(int i = 0; i < result.length; ++i)
            result[i] = items[i].toString();

        return result;
    }

    public static Point asPoint(Location location) {
        return Point.xy(
                (float) location.getLongitude(),
                (float) location.getLatitude()
        );
    }
}
