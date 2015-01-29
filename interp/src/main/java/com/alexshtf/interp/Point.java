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

    public float nrm2() { return x*x + y*y; }
}
