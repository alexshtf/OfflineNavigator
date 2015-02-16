package com.alexshtf.offlinenavigator;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MapImageView extends SubsamplingScaleImageView {
    private IRefreshListener refreshListener;
    private OnTapListener onTapListener;
    private final GestureDetector gd;

    public MapImageView(Context context, AttributeSet attr) {

        super(context, attr);
        this.gd = setupGestureDetector();
    }

    public MapImageView(Context context) {
        super(context);
        this.gd = setupGestureDetector();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gd.onTouchEvent(event))
            return true;

        return super.onTouchEvent(event);
    }

    public void setOnRefreshListener(IRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (refreshListener != null)
            refreshListener.onRefresh();
    }

    private GestureDetector setupGestureDetector() {

        return new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onTapListener != null) {
                    PointF p = viewToSourceCoord(e.getX(), e.getY());
                    onTapListener.onImageTap(MapImageView.this, p.x, p.y);
                }
                return true;
            }
        });
    }

    public static interface IRefreshListener {
        void onRefresh();
    }

    public interface OnTapListener {
        public void onImageTap(View view, float x, float y);
    }
}
