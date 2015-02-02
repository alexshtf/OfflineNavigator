package com.alexshtf.interp;

public class Point {
    private final float x;
    private final float y;

    Point(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float normSquared() { return x*x + y*y; }

    public float norm() { return (float) Math.sqrt(normSquared()); }

    public Point normalized() {
        if (normSquared() > 0) {
            float norm = (float) Math.sqrt(normSquared());
            return new Point(x / norm, y / norm);
        }

        throw new ArithmeticException("Cannot normalize a zero vector");
    }

    public Point scaled(float factor) {
        return new Point(x * factor, y * factor);
    }

    public static Point add(Point p, Point q) {
        return new Point(p.x + q.x, p.y + q.y);
    }

    public static Point sub(Point p, Point q) {
        return new Point(p.x - q.x, p.y - q.y);
    }

    public static Point interpolate(Point p, Point q, float factor) {
        return add(p.scaled(factor), q.scaled(1 - factor));
    }

    public static Point orthogonalTo(Point dir)  {
        return new Point(dir.getY(), -dir.getX());
    }

    public static float innerProduct(Point p, Point q) {
        return p.x * q.x + p.y * q.y;
    }

    public float distanceToSegment(Point segStart, Point segEnd) {
        Point u = sub(segStart, segEnd);
        Point v = sub(this, segEnd);

        float lambda = innerProduct(u, v) / u.normSquared();

        if (lambda < 0)      // we are near segEnd
            return sub(this, segEnd).norm();
        else if (lambda > 1) // we are near segStart
            return sub(this, segStart).norm();
        else                 // we are between segStart and segEnd
            return sub(this, interpolate(segStart, segEnd, lambda)).norm();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Float.compare(point.x, x) != 0) return false;
        if (Float.compare(point.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
