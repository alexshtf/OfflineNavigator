package com.alexshtf.offlinenavigator;

import android.test.AndroidTestCase;


public class LocationInterpolatorTest extends AndroidTestCase {

    public void testEmptyInterpolatorProducesNullLocation()
    {
        LocationInterpolator interpolator = new LocationInterpolator();
        assertEquals(null, interpolator.interpolate(100, 200));
    }

}
