package com.alexshtf.offlinenavigator;

import android.app.Activity;
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
    private Activity activity;

    public AnchorsManager(Activity activity, MatrixNotifyingImageView mapImage, FrameLayout mapLayout, LocationInterpolator locationInterpolator) {
        this.locationInterpolator = locationInterpolator;
        this.mapImage = mapImage;
        this.mapLayout = mapLayout;
        this.anchorIcons = anchorIconsFrom(locationInterpolator);
        this.activity = activity;
    }

    public void updateIconsDisplay() {
        for(ImageView icon : anchorIcons) {
            Point point = (Point) icon.getTag(R.id.POINT_ON_IMAGE_KEY);
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
        Point pointOnImage = Point.xy(imageX, imageY);
        addToInterpolator(pointOnImage);
        addAnchorIcon(pointOnImage);
        updateIconsDisplay();
    }

    private void addAnchorIcon(Point onImage) {
        anchorIcons.add(addAnchorIconAt(onImage));
    }

    private void addToInterpolator(Point onImage) {
        locationInterpolator.addAnchor(onImage, asPoint(lastLocation));
    }

    private List<ImageView> anchorIconsFrom(LocationInterpolator li) {
        List<ImageView> result = new ArrayList<>();

        for(Point point : li.getPointsOnImage())
            result.add(addAnchorIconAt(point));

        return result;
    }

    private ImageView addAnchorIconAt(Point point) {
        ImageView view = new ImageView(activity);
        mapLayout.addView(view);

        view.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Drawable icon = activity.getResources().getDrawable(R.drawable.anchor_icon);
        view.setImageDrawable(icon);

        view.setTag(R.id.WIDTH_KEY, icon.getIntrinsicWidth());
        view.setTag(R.id.HEIGHT_KEY, icon.getIntrinsicHeight());
        view.setTag(R.id.POINT_ON_IMAGE_KEY, point);
        view.setTag(R.id.IS_ANCHOR, true);

        activity.registerForContextMenu(view);

        return view;
    }

    public void updateLocation(Location location) {
        lastLocation = location;
    }

    public boolean canAddAnchor() {
        return lastLocation != null;
    }

    public void removeAnchor(View anchorView) {
        Point pointOnImage = (Point) anchorView.getTag(R.id.POINT_ON_IMAGE_KEY);
        if (pointOnImage == null)
            return;

        locationInterpolator.removeAnchor(pointOnImage);
        mapLayout.removeView(anchorView);
    }
}
