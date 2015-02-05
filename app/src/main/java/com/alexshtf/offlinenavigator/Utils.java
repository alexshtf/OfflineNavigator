package com.alexshtf.offlinenavigator;

import android.location.Location;

import com.alexshtf.interp.Point;

public class Utils {
    public static Point asPoint(Location location) {
        return Point.xy(
                (float) location.getLongitude(),
                (float) location.getLatitude()
        );
    }
}
