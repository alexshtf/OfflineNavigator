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
    private OnRefreshListener onRefreshListener;
    private OnTapListener onTapListener;
    private OnImageReadyListener onImageReadyListener;
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

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public void setOnImageReadyListener(OnImageReadyListener onImageReadyListener) {
        this.onImageReadyListener = onImageReadyListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (onRefreshListener != null)
            onRefreshListener.onRefresh();
    }

    @Override
    protected void onImageReady() {
        super.onImageReady();
        post(new Runnable() {
            @Override
            public void run() {
                if (onImageReadyListener != null)
                    onImageReadyListener.onImageReady();
            }
        });
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

    public static interface OnRefreshListener {
        void onRefresh();
    }

    public static interface OnTapListener {
        public void onImageTap(View view, float x, float y);
    }

    public static interface OnImageReadyListener {
        public void onImageReady();
    }
}
