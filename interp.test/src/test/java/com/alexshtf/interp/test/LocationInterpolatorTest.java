package com.alexshtf.interp.test;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;

import org.junit.Test;

import static com.alexshtf.interp.Point.xy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class LocationInterpolatorTest  {
    LocationInterpolator interpolator = new LocationInterpolator();
    Point[] onMap   = { xy(3, 4), xy(-7, -8), xy(11, 12), xy(6, 7) };
    Point[] onImage = { xy(1, 2), xy(-5, -6), xy(9 , 10), xy(3, 5) };

    @Test
    public void givenNoPointsProducesNullLocation() {
        Point arbitraryPoint = xy(50, 30);
        assertNull(interpolator.interpMapToImage(arbitraryPoint));
    }

    @Test
    public void givenOnePointProducesNullLocation() {
        interpolator.addAnchor(onImage[0], onMap[0]);

        Point arbitraryPoint = xy(60, 70);
        assertNull(interpolator.interpMapToImage(arbitraryPoint));
    }

    @Test
    public void givenTwoPointsProducesNullLocation() {
        interpolator.addAnchor(onImage[0], onMap[0]);
        interpolator.addAnchor(onImage[1], onMap[1]);

        assertNull(interpolator.interpMapToImage(xy(-50, 30)));
    }

    @Test
    public void givenThreePointsProducesNonNullLocation() {
        interpolator.addAnchor(onImage[0], onMap[0]);
        interpolator.addAnchor(onImage[1], onMap[1]);
        interpolator.addAnchor(onImage[2], onMap[2]);

        Point arbitraryPoint = xy(-50, 40);
        assertNotNull(interpolator.interpMapToImage(arbitraryPoint));
    }

    @Test
    public void anchorsAreInterpolatedToThemselves() {
        for(int i = 0; i < 3; ++i)
            interpolator.addAnchor(onImage[i], onMap[i]);

        for(int i = 0; i < 3; ++i)
            assertThat("i = " + i, interpolator.interpMapToImage(onMap[i]), equalTo(onImage[i]));
    }

    @Test
    public void canRemoveAnchorPoints() {
        for(int i = 0; i < 3; ++i)
            interpolator.addAnchor(onImage[i], onMap[i]);

        Point beforeAdd = interpolator.interpMapToImage(onMap[3]);

        interpolator.addAnchor(onImage[3], onMap[3]);
        Point afterAdd = interpolator.interpMapToImage(onMap[3]);

        interpolator.removeAnchor(onImage[3]);
        Point afterRemove = interpolator.interpMapToImage(onMap[3]);

        assertThat(afterAdd, not(equalTo(beforeAdd)));
        assertThat(beforeAdd, equalTo(afterRemove));
    }
}