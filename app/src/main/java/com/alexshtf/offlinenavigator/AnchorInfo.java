package com.alexshtf.offlinenavigator;

import com.alexshtf.interp.Point;

/**
* Created by alexshtf on 11/02/2015.
*/
public class AnchorInfo {
    private final Point poinOnImage;
    private final Point pointOnMap;

    public AnchorInfo(Point poinOnImage, Point pointOnMap) {
        this.poinOnImage = poinOnImage;
        this.pointOnMap = pointOnMap;
    }

    public Point getPoinOnImage() {
        return poinOnImage;
    }

    public Point getPointOnMap() {
        return pointOnMap;
    }
}
