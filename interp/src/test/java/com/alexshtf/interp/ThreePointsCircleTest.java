package com.alexshtf.interp;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class ThreePointsCircleTest {

    // centered at (1, 1), with radius 5
    private ThreePointsCircle circle = new ThreePointsCircle(
            new Point(4, 5),
            new Point(-3, 4),
            new Point(1, 6)
    );

    @Theory
    public void returnsTrueForPointsInsideCircle(@LinSpace(first = 0, last = 4.99, count = 100) double radius,
                                                 @LinSpace(first = 0, last = 2 * Math.PI, count = 100) double angle) {

        float x = (float)(1 + radius * Math.cos(angle));
        float y = (float)(1 + radius * Math.sin(angle));

        assertTrue(getMessage(radius, angle, x, y), circle.isInside(new Point(x, y)));
    }

    @Theory
    public void returnsFalseForPointsOutsideCircle(@LinSpace(first = 5.01, last = 100, count = 100) double radius,
                                                   @LinSpace(first = 0, last = 2 * Math.PI, count = 100) double angle)
    {
        float x = (float)(1 + radius * Math.cos(angle));
        float y = (float)(1 + radius * Math.sin(angle));

        assertFalse(getMessage(radius, angle, x, y), circle.isInside(new Point(x, y)));
    }

    private String getMessage(double radius, double angle, float x, float y) {
        return String.format("x = %f, y = %f, radius = %f, angle = %f", x, y, radius, angle);
    }
}
