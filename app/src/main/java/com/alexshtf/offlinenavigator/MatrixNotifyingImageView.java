package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.util.AttributeSet;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class MatrixNotifyingImageView extends ImageViewTouch {

    private ImageMatrixChangedListener listener;

    public MatrixNotifyingImageView(Context context) {
        super(context);
    }

    public MatrixNotifyingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MatrixNotifyingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageMatrixChangedListener(ImageMatrixChangedListener listener) {
        this.listener = listener;
    }

    public interface ImageMatrixChangedListener {
        void onChanged();
    }

    @Override
    protected void onImageMatrixChanged() {
        super.onImageMatrixChanged();

        if (null != listener)
            listener.onChanged();
    }
}
