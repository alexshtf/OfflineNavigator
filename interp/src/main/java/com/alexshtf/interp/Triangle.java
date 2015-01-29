package com.alexshtf.interp;

public class Triangle {
    private Point[] pts;

    public Triangle(Point a, Point b, Point c) {
        pts = new Point[]{a, b, c};
    }

    /**
     * Computes the Euclidean distance from the given point to the given triangle. If the point is
     * inside the triangle, the distance is 0.
     * @param x The point
     * @return The distance to the triangle.
     */
    public float distance(Point x) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Computes the barycentric coordinates of the given point.
     * @param x The point to compute coordinates for.
     * @return An array of 3 coefficients such that x is the linear combination of the triangle's
     * points returned by {@link this.at(int)}
     */
    public float[] barycentricCoordinates(Point x) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns the i-th point of the triangle.
     * @param i The zero-based index of the point. Must be 0, 1, or 2.
     * @return The point at index i.
     */
    public Point at(int i) {
        return pts[i];
    }
}
