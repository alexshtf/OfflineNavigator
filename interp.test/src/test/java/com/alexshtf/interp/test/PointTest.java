package com.alexshtf.interp.test;

import com.alexshtf.interp.Point;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.alexshtf.interp.Point.add;
import static com.alexshtf.interp.Point.innerProduct;
import static com.alexshtf.interp.Point.interpolate;
import static com.alexshtf.interp.Point.orthogonalTo;
import static com.alexshtf.interp.Point.sub;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class PointTest {

    Point p = new Point(1, 3);
    Point q = new Point(5, 6);

    @Test
    public void canAdd() {
        assertThat(add(p, q), equalTo(new Point(6, 9)));
    }

    @Test
    public void canSubstract() {
        assertThat(sub(p, q), equalTo(new Point(-4, -3)));
    }

    @Test
    public void canInterpolate() {
        assertThat(interpolate(p, q, 0.5f), equalTo(new Point(3, 4.5f)));
    }

    @Theory
    public void orthotonalToProducesOrthogonalResults(
            @LinSpace(first = -5, last = 5) double x,
            @LinSpace(first = -5, last = 5) double y) {
        Point dir = new Point((float)x, (float)y);
        assertThat(innerProduct(dir, orthogonalTo(dir)), equalTo(0f));
    }

    @Test
    public void orthogonalToObeysCounterclockwiseDirection()
    {
        final Point left = new Point(-1, 0);
        final Point down = new Point(0, -1);
        final Point up = new Point(0, 1);

        assertThat(dist(orthogonalTo(left), up), equalTo(0f));
        assertThat(dist(orthogonalTo(down), left), equalTo(0f));
    }

    @Test
    public void computesDistanceToSegment() {
        Point segStart = new Point(-1, 1);
        Point segEnd = new Point(1, 1);

        assertThat(segStart.distanceToSegment(segStart, segEnd), equalTo(0.0f));
        assertThat(segEnd.distanceToSegment(segStart, segEnd), equalTo(0.0f));
        assertThat(interpolate(segStart, segEnd, 0.5f).distanceToSegment(segStart, segEnd), equalTo(0.0f));
        assertThat(pnt(-2, 1).distanceToSegment(segStart, segEnd), equalTo(1f));
        assertThat(pnt(2, 1).distanceToSegment(segStart, segEnd), equalTo(1f));
        assertThat(pnt(0, 0).distanceToSegment(segStart, segEnd), equalTo(1f));
    }

    static Point pnt(float x, float y) {
        return new Point(x, y);
    }
    static float dist(Point x, Point y) { return sub(x, y).norm(); }
}
