package com.alexshtf.interp.test;

import com.alexshtf.interp.Point;
import com.alexshtf.interp.Triangle;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.alexshtf.interp.Point.add;
import static com.alexshtf.interp.Point.interpolate;
import static com.alexshtf.interp.Point.orthogonalTo;
import static com.alexshtf.interp.Point.sub;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TriangleTest {
    static final float PRECISELY = 0;

    final Point a = new Point(1, 2);
    final Point b = new Point(3, 3);
    final Point c = new Point(-1, 2);

    // triangle points, in counter-clockwise direction
    final Triangle triangle = new Triangle(a, b, c);

    @Test
    public void retrievesPoints() {
        assertEquals(triangle.at(0), a);
        assertEquals(triangle.at(1), b);
        assertEquals(triangle.at(2), c);
    }

    @Test
    public void distanceToVerticesIsZero() {
        assertEquals(0, triangle.distance(a), PRECISELY);
        assertEquals(0, triangle.distance(b), PRECISELY);
        assertEquals(0, triangle.distance(c), PRECISELY);
    }

    @Theory
    public void producesCorrectBarycentricCoordinates(
            @LinSpace(first = -1, last = 1, count = 100) double a,
            @LinSpace(first = -1, last = 1, count = 100) double b) {
        final double TOLERANCE = 1E-5f;

        double c = 1 - a - b;
        float[] coordinates = triangle.barycentric(fromBarycentric(triangle, a, b, c));

        assertThat((double) coordinates[0], closeTo(a, TOLERANCE));
        assertThat((double) coordinates[1], closeTo(b, TOLERANCE));
        assertThat((double) coordinates[2], closeTo(c, TOLERANCE));
    }

    @Test
    public void distanceOutsideTriangleIsCorrect() {
        final float precision = 1E-5f;

        Point nearAB = walkOrthogonallyFromEdge(a, b, 15);
        Point nearBC = walkOrthogonallyFromEdge(b, c, 17);
        Point nearCA = walkOrthogonallyFromEdge(c, a, 20);

        assertEquals(15, triangle.distance(nearAB), precision);
        assertEquals(17, triangle.distance(nearBC), precision);
        assertEquals(20, triangle.distance(nearCA), precision);
    }


    @Test
    public void distanceToPointsInsideTriangleIsZero() {
        Point inside = new Point(
                (a.getX() + b.getX() + c.getX()) / 3,
                (a.getY() + b.getY() + c.getY()) / 3
        );
        Point onAB = new Point(
                0.25f * a.getX() + 0.75f * b.getX(),
                0.25f * a.getY() + 0.75f * b.getY()
        );
        Point onBC = new Point(
                0.5f * b.getX() + 0.5f * c.getX(),
                0.5f * b.getY() + 0.5f * c.getY()
        );
        Point onAC = new Point(
                0.75f * a.getX() + 0.25f * c.getX(),
                0.75f * a.getY() + 0.25f * c.getY()
        );

        assertEquals(0, triangle.distance(inside), PRECISELY);
        assertEquals(0, triangle.distance(onAB), PRECISELY);
        assertEquals(0, triangle.distance(onBC), PRECISELY);
        assertEquals(0, triangle.distance(onAC), PRECISELY);
    }

    @Test
    public void computesSignedArea() {
        Triangle counterClockwise = triangle;
        Triangle clockwise = new Triangle(a, c, b);

        assertThat(counterClockwise.signedArea() + clockwise.signedArea(), equalTo(0f));
        assertThat(counterClockwise.signedArea(), greaterThan(0f));
        assertThat(clockwise.signedArea(), lessThan(0f));
    }

    private static Point walkOrthogonallyFromEdge(Point a, Point b, float distance) {
        return add(
                between(a, b),
                orthogonalTo(sub(b, a)).normalized().scaled(distance)
        );
    }

    private static Point between(Point p, Point q) {
        return interpolate(p, q, 0.5f);
    }

    private static Point fromBarycentric(Triangle t, double a, double b, double c) {
        Point p = t.at(0).scaled((float)a);
        Point q = t.at(1).scaled((float)b);
        Point r = t.at(2).scaled((float)c);

        return add(add(p, q), r);
    }
}
