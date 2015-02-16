package com.alexshtf.offlinenavigator;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.alexshtf.offlinenavigator.Utils.asPoint;

class AnchorsManager {
    private final LocationInterpolator locationInterpolator;
    private final SubsamplingScaleImageView mapImage;
    private final FrameLayout mapLayout;
    private Location lastKnownLocation;
    private Activity activity;

    public AnchorsManager(Activity activity, SubsamplingScaleImageView mapImage, FrameLayout mapLayout, LocationInterpolator locationInterpolator) {
        this.locationInterpolator = locationInterpolator;
        this.mapImage = mapImage;
        this.mapLayout = mapLayout;
        this.activity = activity;
    }

    public void updateIconsDisplay() {
        for(int i = 0; i < mapLayout.getChildCount(); ++i) {
            View child = mapLayout.getChildAt(i);
            if (isAnchor(child))
                updateIconDisplay(child);
        }
    }

    public AnchorInfo addAnchorAtLastKnownLocation(float imageX, float imageY) {
        AnchorInfo anchorInfo = new AnchorInfo(
                Point.xy(imageX, imageY),
                asPoint(lastKnownLocation)
        );
        addAnchor(anchorInfo);
        return anchorInfo;
    }

    public void addAnchor(AnchorInfo anchorInfo) {
        addAnchor(anchorInfo.getPoinOnImage(), anchorInfo.getPointOnMap());
    }

    private void addAnchor(Point pointOnImage, Point pointOnMap) {
        addAnchorIconAt(pointOnImage);
        updateInterpolator(pointOnImage, pointOnMap);
        updateIconsDisplay();
    }


    public void removeAnchor(View anchorView) {
        Point pointOnImage = pointOnImageOf(anchorView);
        locationInterpolator.removeAnchor(pointOnImage);
        mapLayout.removeView(anchorView);
    }

    public void updateLocation(Location location) {
        lastKnownLocation = location;
    }

    public boolean canAddAnchor() {
        return lastKnownLocation != null;
    }

    public static boolean isAnchor(View view) {
        return view.getTag(R.id.IS_ANCHOR) != null;
    }

    private void updateInterpolator(Point onImage, Point pointOnMap) {
        locationInterpolator.addAnchor(onImage, pointOnMap);
    }

    private void addAnchorIconAt(Point point) {
        ImageView view = new ImageView(activity);
        view.setLayoutParams(new RelativeLayout.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT
        ));

        Drawable icon = activity.getResources().getDrawable(R.drawable.anchor_icon);
        view.setImageDrawable(icon);

        setIconDimensions(view, icon);
        setPointOnImage(view, point);
        markAsAnchor(view);

        activity.registerForContextMenu(view);
        mapLayout.addView(view);
    }

    private void updateIconDisplay(View anchorView) {
        Point point = pointOnImageOf(anchorView);
        Utils.repositionIcon(
                mapImage, anchorView,
                point.getX(), point.getY(),
                imageWidthOf(anchorView), imageHeightOf(anchorView)
        );
        anchorView.setVisibility(View.VISIBLE);
    }


    private static void markAsAnchor(View anchorView) {
        anchorView.setTag(R.id.IS_ANCHOR, true);
    }

    private static void setPointOnImage(View anchorView, Point p) {
        anchorView.setTag(R.id.POINT_ON_IMAGE_KEY, p);
    }

    private static void setIconDimensions(View anchorView, Drawable icon) {
        anchorView.setTag(R.id.WIDTH_KEY, icon.getIntrinsicWidth());
        anchorView.setTag(R.id.HEIGHT_KEY, icon.getIntrinsicHeight());
    }

    public static Point pointOnImageOf(View anchorView) {
        return (Point) anchorView.getTag(R.id.POINT_ON_IMAGE_KEY);
    }

    private static int imageHeightOf(View anchorView) {
        return (Integer) anchorView.getTag(R.id.HEIGHT_KEY);
    }

    private static int imageWidthOf(View anchorView) {
        return (Integer) anchorView.getTag(R.id.WIDTH_KEY);
    }
}
