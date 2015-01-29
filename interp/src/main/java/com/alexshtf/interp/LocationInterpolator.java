package com.alexshtf.interp;


import java.util.ArrayList;

public class LocationInterpolator {
    ArrayList<Point> onImage = new ArrayList<>();
    ArrayList<Point> onMap = new ArrayList<>();

    public Point interpolate(float x, float y) {
        if (onImage.size() > 2)
            return onImage.get(0);
        else
            return null;
    }

    public void add(Point onImage, Point onMap) {
        this.onImage.add(onImage);
        this.onMap.add(onMap);
    }
}
