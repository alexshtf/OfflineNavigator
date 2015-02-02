package com.alexshtf.interp;

import org.junit.Test;

import static com.alexshtf.interp.Point.add;
import static com.alexshtf.interp.Point.interpolate;
import static com.alexshtf.interp.Point.orthogonalTo;
import static com.alexshtf.interp.Point.sub;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

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

    @Test
    public void orthogonalToObeysCounterclockwiseDirection()
    {
        Point left = new Point(-1, 0);
        Point down = new Point(0, -1);
        Point up = new Point(0, 1);

        assertThat(orthogonalTo(left), equalTo(up));
        assertThat(orthogonalTo(down), equalTo(left));
    }
}
