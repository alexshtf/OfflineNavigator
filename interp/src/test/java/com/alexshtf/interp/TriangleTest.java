package com.alexshtf.interp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TriangleTest {
    Triangle triangle = new Triangle(
            new Point(1, 2),
            new Point(-1, 2),
            new Point(3, 3)
    );

    @Test
    public void retrievesPoints() {

        assertEquals(triangle.at(0), new Point(1, 2));
        assertEquals(triangle.at(1), new Point(-1, 2));
        assertEquals(triangle.at(2), new Point(3, 3));
    }

    @Test
    public void distanceToPointsInsideTriangleIsZero() {
        // TODO
    }
}
