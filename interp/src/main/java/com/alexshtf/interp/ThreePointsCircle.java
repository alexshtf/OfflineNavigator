package com.alexshtf.interp;

import org.ejml.alg.fixed.FixedOps3;
import org.ejml.alg.fixed.FixedOps4;
import org.ejml.data.FixedMatrix3x3_64F;
import org.ejml.data.FixedMatrix4x4_64F;

class ThreePointsCircle {
    private final Point a;
    private final Point b;
    private final Point c;

    ThreePointsCircle(Point a, Point b, Point c)
    {
        if (areClockwise(a, b, c)) {
            this.a = a;
            this.b = c;
            this.c = b;
        }
        else {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    boolean isInside(Point d) {
        FixedMatrix4x4_64F mat = new FixedMatrix4x4_64F(
                a.getX(), a.getY(), a.normSquared(), 1,
                b.getX(), b.getY(), b.normSquared(), 1,
                c.getX(), c.getY(), c.normSquared(), 1,
                d.getX(), d.getY(), d.normSquared(), 1);

        double det = FixedOps4.det(mat);
        if (det < 0)
            return false;
        else
            return true;
    }

    private static boolean areClockwise(Point a, Point b, Point c)
    {
        FixedMatrix3x3_64F mat = new FixedMatrix3x3_64F(
                1, a.getX(), a.getY(),
                1, b.getX(), b.getY(),
                1, c.getX(), c.getY()
        );

        return FixedOps3.det(mat) < 0;
    }
}
