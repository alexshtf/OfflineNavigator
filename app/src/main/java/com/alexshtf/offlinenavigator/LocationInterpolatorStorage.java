package com.alexshtf.offlinenavigator;

import android.os.Bundle;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;

import java.util.Arrays;

public class LocationInterpolatorStorage {


    public static final String ON_MAP_X = "ON_MAP_X";
    public static final String ON_MAP_Y = "ON_MAP_Y";
    public static final String ON_IMAGE_X = "ON_IMAGE_X";
    public static final String ON_IMAGE_Y = "ON_IMAGE_Y";

    public static void toBundle(LocationInterpolator li, Bundle bundle) {
        bundle.putFloatArray(ON_MAP_X, onMapX(li));
        bundle.putFloatArray(ON_MAP_Y, onMapY(li));
        bundle.putFloatArray(ON_IMAGE_X, onImageX(li));
        bundle.putFloatArray(ON_IMAGE_Y, onImageY(li));
    }

    public static LocationInterpolator fromBundle(Bundle bundle) {
        LocationInterpolator result = new LocationInterpolator();
        if (bundle == null)
            return result;

        float[] onMapX = bundle.getFloatArray(ON_MAP_X);
        float[] onMapY = bundle.getFloatArray(ON_MAP_Y);
        float[] onImageX = bundle.getFloatArray(ON_IMAGE_X);
        float[] onImageY = bundle.getFloatArray(ON_IMAGE_Y);

        if (Arrays.asList(onMapX, onMapY, onImageX, onImageY).contains(null))
            return result;

        for(int i = 0; i < onMapX.length; ++i)
            result.addAnchor(
                    new Point(onMapX[i], onMapY[i]),
                    new Point(onImageX[i], onImageY[i])
            );

        return result;
    }

    private static float[] onMapX(final LocationInterpolator li) {
        return getNumbers(li.getAnchorsCount(), new Getter() {
            @Override
            public float at(int i) {
                return li.getPointsOnMap().get(i).getX();
            }
        });
    }

    private static float[] onMapY(final LocationInterpolator li) {
        return getNumbers(li.getAnchorsCount(), new Getter() {
            @Override
            public float at(int i) {
                return li.getPointsOnMap().get(i).getY();
            }
        });
    }

    private static float[] onImageX(final LocationInterpolator li) {
        return getNumbers(li.getAnchorsCount(), new Getter() {
            @Override
            public float at(int i) {
                return li.getPointsOnImage().get(i).getX();
            }
        });
    }

    private static float[] onImageY(final LocationInterpolator li) {
        return getNumbers(li.getAnchorsCount(), new Getter() {
            @Override
            public float at(int i) {
                return li.getPointsOnImage().get(i).getY();
            }
        });
    }

    private static float[] getNumbers(int n, Getter getter) {
        float[] result = new float[n];
        for(int i = 0; i < n; ++i)
            result[i] = getter.at(i);
        return result;
    }

    private interface Getter {
        float at(int i);
    }
}
