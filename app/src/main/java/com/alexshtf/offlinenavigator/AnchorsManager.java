package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;

import java.util.ArrayList;
import java.util.List;

import static com.alexshtf.offlinenavigator.Utils.asPoint;

/**
* Created by alexshtf on 05/02/2015.
*/
class AnchorsManager {
    private final LocationInterpolator locationInterpolator;
    private final List<ImageView> anchorIcons;
    private final MatrixNotifyingImageView mapImage;
    private final FrameLayout mapLayout;
    private Location lastLocation;
    private Context context;

    public AnchorsManager(Context context, MatrixNotifyingImageView mapImage, FrameLayout mapLayout, LocationInterpolator locationInterpolator) {
        this.locationInterpolator = locationInterpolator;
        this.mapImage = mapImage;
        this.mapLayout = mapLayout;
        this.anchorIcons = anchorIconsFrom(locationInterpolator);
        this.context = context;
    }

    public void updateIconsDisplay() {
        for(ImageView icon : anchorIcons) {
            Point point = (Point) icon.getTag(R.id.POINT_KEY);
            int w = (Integer) icon.getTag(R.id.WIDTH_KEY);
            int h = (Integer) icon.getTag(R.id.HEIGHT_KEY);

            float[] xy = { point.getX(), point.getY() };
            mapImage.getImageViewMatrix().mapPoints(xy);

            icon.setTranslationX(xy[0] - 0.5f * w);
            icon.setTranslationY(xy[1] - 0.5f * h);
            icon.setVisibility(View.VISIBLE);
        }
    }

    public void addAnchor(float imageX, float imageY) {
        Point onImage = Point.xy(imageX, imageY);
        locationInterpolator.addAnchor(onImage, asPoint(lastLocation));
        anchorIcons.add(addAnchorIconAt(onImage));
        updateIconsDisplay();
    }

    private List<ImageView> anchorIconsFrom(LocationInterpolator li) {
        List<ImageView> result = new ArrayList<>();

        for(Point point : li.getPointsOnImage())
            result.add(addAnchorIconAt(point));

        return result;
    }

    private ImageView addAnchorIconAt(Point point) {
        ImageView view = new ImageView(context);
        mapLayout.addView(view);

        view.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Drawable icon = context.getResources().getDrawable(R.drawable.anchor_icon);
        view.setImageDrawable(icon);

        view.setTag(R.id.WIDTH_KEY, icon.getIntrinsicWidth());
        view.setTag(R.id.HEIGHT_KEY, icon.getIntrinsicHeight());
        view.setTag(R.id.POINT_KEY, point);

        return view;
    }

    public void updateLocation(Location location) {
        lastLocation = location;
    }

    public boolean canAddAnchor() {
        return lastLocation != null;
    }
}
