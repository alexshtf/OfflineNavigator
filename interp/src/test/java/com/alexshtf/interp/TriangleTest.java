package com.alexshtf.interp;

import org.junit.Test;

import static com.alexshtf.interp.Point.add;
import static com.alexshtf.interp.Point.interpolate;
import static com.alexshtf.interp.Point.orthogonalTo;
import static com.alexshtf.interp.Point.sub;
import static org.junit.Assert.assertEquals;

public class TriangleTest {
    static final float PRECISELY = 0;

    Point a = new Point(1, 2);
    Point b = new Point(-1, 2);
    Point c = new Point(3, 3);

    Triangle triangle = new Triangle(a, b, c);

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

    @Test
    public void distanceOutsideTriangleIsCorrect()
    {
        final float precision = 1E-5f;

        Point nearAB = walkOnLine(between(a, b), orthogonalTo(sub(a, b)), 15);
        Point nearBC = walkOnLine(between(b, c), orthogonalTo(sub(b, c)), 17);
        Point nearCA = walkOnLine(between(c, a), orthogonalTo(sub(c, a)), 20);

        assertEquals(15, triangle.distance(nearAB), precision);
        assertEquals(17, triangle.distance(nearBC), precision);
        assertEquals(20, triangle.distance(nearCA), precision);
    }

    private static Point walkOnLine(Point start, Point dir, float distance) {
        return add(start, dir.normalized().scaled(distance));
    }

    private static Point between(Point p, Point q) {
        return interpolate(p, q, 0.5f);
    }

//
//    @Test
//    public void distanceToPointsInsideTriangleIsZero() {
//        Point inside = new Point(
//                (a.getX() + b.getX() + c.getX()) / 3,
//                (a.getY() + b.getY() + c.getY()) / 3
//        );
//        Point onAB = new Point(
//                0.25f * a.getX() + 0.75f * b.getX(),
//                0.25f * a.getY() + 0.75f * b.getY()
//        );
//        Point onBC = new Point(
//                0.5f * b.getX() + 0.5f * c.getX(),
//                0.5f * b.getY() + 0.5f * c.getY()
//        );
//        Point onAC = new Point(
//                0.75f * a.getX() + 0.25f * c.getX(),
//                0.75f * a.getY() + 0.25f * c.getY()
//        );
//
//        assertEquals(0, triangle.distance(inside), PRECISELY);
//        assertEquals(0, triangle.distance(onAB), PRECISELY);
//        assertEquals(0, triangle.distance(onBC), PRECISELY);
//        assertEquals(0, triangle.distance(onAC), PRECISELY);
//    }
}
