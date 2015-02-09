package com.alexshtf.interp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LocationInterpolator {
    private List<Point> onImage = new ArrayList<>();
    private List<Point> onMap = new ArrayList<>();
    private List<Triangle> trianglesOnMap = new ArrayList<>();

    public Point interpMapToImage(Point onMap) {
        if (trianglesOnMap.size() == 0)
            return null;

        Triangle t = findNearestTriangle(trianglesOnMap, onMap);
        Point onImage = barycentricInterpolation(t, onMap, this.onImage);
        return onImage;
    }

    public void addAnchor(Point onImage, Point onMap) {
        this.onImage.add(onImage);
        this.onMap.add(onMap);
        this.trianglesOnMap = computeTriangulation(this.onMap);
    }

    public void removeAnchor(Point onImage) {
        int index = this.onImage.indexOf(onImage);
        this.onImage.remove(index);
        this.onMap.remove(index);
        this.trianglesOnMap = computeTriangulation(this.onMap);
    }

    public int getAnchorsCount() { return onImage.size(); }
    public List<Point> getPointsOnImage() { return Collections.unmodifiableList(onImage); }
    public List<Point> getPointsOnMap() { return Collections.unmodifiableList(onMap); }

    private static ArrayList<Triangle> computeTriangulation(List<Point> points) {
        ArrayList<Triangle> triangles = new ArrayList<>();

        final float AREA_TOLERANCE = 1E-16f;

        int n = points.size();
        for(int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    Point pi = points.get(i);
                    Point pj = points.get(j);
                    Point pk = points.get(k);

                    Triangle triangle = new Triangle(pi, pj, pk);
                    if (triangle.signedArea() <= AREA_TOLERANCE)
                        continue;

                    ThreePointsCircle circle = new ThreePointsCircle(pi, pj, pk);
                    if (circleContainsNonVertex(circle, triangle, points))
                        continue;

                    triangle.setTag(new int[] { i, j, k });
                    triangles.add(triangle);
                }
            }
        }

        return triangles;
    }

    private static Point barycentricInterpolation(Triangle mapTriangle, Point onMap, List<Point> onImage) {
        float[] coordinates = mapTriangle.barycentric(onMap);
        int[] indices = mapTriangle.getTag();

        Point p0 = onImage.get(indices[0]).scaled(coordinates[0]);
        Point p1 = onImage.get(indices[1]).scaled(coordinates[1]);
        Point p2 = onImage.get(indices[2]).scaled(coordinates[2]);

        return Point.add(Point.add(p0, p1), p2);
    }

    private static Triangle findNearestTriangle(List<Triangle> triangles, final Point p) {
        return Collections.min(triangles, new Comparator<Triangle>() {
            @Override
            public int compare(Triangle o1, Triangle o2) {
                return Float.compare(o1.distance(p), o2.distance(p));
            }
        });
    }

    private static boolean circleContainsNonVertex(ThreePointsCircle circle, Triangle triangle, List<Point> points) {
        for(int i = 0; i < points.size(); ++i)  {
            Point p = points.get(i);

            if (isVertex(p, triangle))
                continue;

            if (circle.isInside(p))
                return true;
        }

        return false;
    }

    private static boolean isVertex(Point p, Triangle triangle) {
        return p == triangle.at(0) || p == triangle.at(1) || p == triangle.at(2);
    }
}
