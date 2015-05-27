package com.alexshtf.interp;

public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public static Point xy(double x, double y) {
        return new Point(x, y);
    }

    public static double distSquared(Point onImage, Point interpOnMap) {
        return sub(interpOnMap, onImage).normSquared();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double normSquared() { return x*x + y*y; }

    public double norm() { return Math.sqrt(normSquared()); }

    public Point normalized() {
        if (normSquared() > 0) {
            double norm = Math.sqrt(normSquared());
            return new Point(x / norm, y / norm);
        }

        throw new ArithmeticException("Cannot normalize a zero vector");
    }

    public Point scaled(double factor) {
        return new Point(x * factor, y * factor);
    }

    public static Point add(Point p, Point q) {
        return new Point(p.x + q.x, p.y + q.y);
    }

    public static Point sub(Point p, Point q) {
        return new Point(p.x - q.x, p.y - q.y);
    }

    public static Point interpolate(Point p, Point q, double factor) {
        return add(p.scaled(factor), q.scaled(1 - factor));
    }

    public static Point orthogonalTo(Point dir)  {
        return new Point(dir.getY(), -dir.getX());
    }

    public static double innerProduct(Point p, Point q) {
        return p.x * q.x + p.y * q.y;
    }

    public double distanceToSegment(Point segStart, Point segEnd) {
        Point u = sub(segStart, segEnd);
        Point v = sub(this, segEnd);

        double lambda = innerProduct(u, v) / u.normSquared();

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

        if (Double.compare(point.x, x) != 0) return false;
        if (Double.compare(point.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? (int)Double.doubleToLongBits(x) : 0);
        result = 31 * result + (y != +0.0f ? (int)Double.doubleToLongBits(y) : 0);
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
