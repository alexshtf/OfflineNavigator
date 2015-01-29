package com.alexshtf.interp;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LocationInterpolatorTest  {
    LocationInterpolator interpolator = new LocationInterpolator();

    @Test
    public void givenNoPointsProducesNullLocation() {
        assertNull(interpolator.interpolate(50, 30));
    }

    @Test
    public void givenOnePointProducesNullLocation() {
        interpolator.add(new Point(1, 2), new Point(3, 4));
        assertNull(interpolator.interpolate(60, 70));
    }

    @Test
    public void givenTwoPointsProducesNullLocation()
    {
        interpolator.add(new Point(1, 2), new Point(3, 4));
        interpolator.add(new Point(5, 6), new Point(7, 8));
        assertNull(interpolator.interpolate(-50, 30));
    }

    @Test
    public void givenThreePointsProducesNonNullLocation()
    {
        interpolator.add(new Point(1, 2), new Point(3, 4));
        interpolator.add(new Point(5, 6), new Point(7, 8));
        interpolator.add(new Point(9, 10), new Point(11, 12));
        assertNotNull(interpolator.interpolate(-50, 40));
    }
}