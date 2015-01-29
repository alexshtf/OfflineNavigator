package com.alexshtf.interp;



public class LocationInterpolator {
    private int count;

    public Point interpolate(float x, float y) {
        if (count > 2)
            return new Point(0, 0);
        else
            return null;
    }

    public void add(Point point, Point point1) {
        ++count;
    }
}
