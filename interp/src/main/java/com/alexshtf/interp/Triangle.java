package com.alexshtf.interp;

import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.Comparator;

public class Triangle {
    private Point[] pts;
    private Object tag;

    public Triangle(Point a, Point b, Point c) {
        pts = new Point[]{a, b, c};
    }

    /**
     * Computes the Euclidean distance from the given point to the given triangle. If the point is
     * inside the triangle, the distance is 0.
     * @param x The point
     * @return The distance to the triangle.
     */
    public float distance(final Point x) {
        if (isInside(x))
            return 0;

        Point[][] triangleEdges = {
                {pts[0], pts[1]},
                {pts[1], pts[2]},
                {pts[2], pts[0]}
        };
        Arrays.sort(triangleEdges, new CompareDistanceToEdgeFrom(x));
        Point[] closestEdge = triangleEdges[0];

        return x.distanceToSegment(closestEdge[0], closestEdge[1]);
    }

    /**
     * Computes the signed area of the triangle. Clockwise triangles have negative area,
     * and counter-clockwise triangles have positive area.
     * @return
     */
    public float signedArea() {
        return (float) getAreaMatrix().determinant();
    }

    private boolean isInside(Point x) {
        float[] coordinates = barycentric(x);

        Arrays.sort(coordinates);
        float min = coordinates[0];
        float max = coordinates[2];

        return min >= 0 && max <= 1;
    }

    /**
     * Computes the barycentric coordinates of the given point.
     * @param a The point to compute coordinates for.
     * @return An array of 3 coefficients such that x is the linear combination of the triangle's
     * points returned by {@link this.at(int)}, and the coordinates sum to 1.
     */
    public float[] barycentric(Point a) {

        SimpleMatrix vec = new SimpleMatrix(new double[][] {
                {a.getX()},
                {a.getY()},
                {1}
        });

        SimpleMatrix result = getAreaMatrix().pseudoInverse().mult(vec);

        return new float[]{
                (float) result.get(0, 0),
                (float) result.get(1, 0),
                (float) result.get(2, 0)
        };
    }

    /**
     * Returns the i-th point of the triangle.
     * @param i The zero-based index of the point. Must be 0, 1, or 2.
     * @return The point at index i.
     */
    public Point at(int i) {
        return pts[i];
    }

    public <T> T getTag() {
        return (T) tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    private SimpleMatrix getAreaMatrix() {
        return new SimpleMatrix(new double[][] {
                {pts[0].getX(), pts[1].getX(), pts[2].getX()},
                {pts[0].getY(), pts[1].getY(), pts[2].getY()},
                {1            , 1            , 1            }
        });
    }

    private static class CompareDistanceToEdgeFrom implements Comparator<Point[]> {
        private final Point x;

        public CompareDistanceToEdgeFrom(Point x) {
            this.x = x;
        }

        @Override
        public int compare(Point[] left, Point[] right) {
            float dLeft = x.distanceToSegment(left[0], left[1]);
            float dRight = x.distanceToSegment(right[0], right[1]);
            return Float.compare(dLeft, dRight);
        }
    }
}
