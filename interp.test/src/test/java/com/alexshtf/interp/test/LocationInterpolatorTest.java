package com.alexshtf.interp.test;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LocationInterpolatorTest  {
    LocationInterpolator interpolator = new LocationInterpolator();

    @Test
    public void givenNoPointsProducesNullLocation() {
        assertNull(interpolator.interpolate(new Point(50, 30)));
    }

    @Test
    public void givenOnePointProducesNullLocation() {
        interpolator.addAnchor(new Point(1, 2), new Point(3, 4));
        assertNull(interpolator.interpolate(new Point(60, 70)));
    }

    @Test
    public void givenTwoPointsProducesNullLocation()
    {
        interpolator.addAnchor(new Point(1, 2), new Point(3, 4));
        interpolator.addAnchor(new Point(5, 6), new Point(7, 8));
        assertNull(interpolator.interpolate(new Point(-50, 30)));
    }

    @Test
    public void givenThreePointsProducesNonNullLocation()
    {
        interpolator.addAnchor(new Point(1, 2), new Point(3, 4));
        interpolator.addAnchor(new Point(-5, -6), new Point(-7, -8));
        interpolator.addAnchor(new Point(9, 10), new Point(11, 12));
        assertNotNull(interpolator.interpolate(new Point(-50, 40)));
    }
}