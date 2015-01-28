package com.alexshtf.offlinenavigator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;

import java.io.IOException;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


public class NavigateActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_FILE_KEY = "MAP_IMAGE_FILE";

    private ImageViewTouch mapImage;
    private ToggleButton iAmHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        findViews();
        showImageFromIntent();
        setupIAmHere();
    }

    private void findViews() {
        mapImage = (ImageViewTouch) findViewById(R.id.map_image);
        iAmHere = (ToggleButton) findViewById(R.id.i_am_here);
    }

    private void setupIAmHere() {
        mapImage.setOnTouchListener(new ImageTapListener());
    }

    class ImageTapListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener
    {
        GestureDetector gestureDetector = new GestureDetector(NavigateActivity.this, this);

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            if (iAmHere.isChecked()) {
                Matrix imageViewInv = new Matrix();
                mapImage.getImageViewMatrix().invert(imageViewInv);

                float[] xy = { event.getX(), event.getY() };
                imageViewInv.mapPoints(xy);

                Log.d("", "X = " + xy[0] + ", Y = " + xy[1]);

                iAmHere.setChecked(false);
            }

            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return false;
        }
    }

    private void showImageFromIntent() {
        String imageFile = getIntent().getStringExtra(MAP_IMAGE_FILE_KEY);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageFile));
            mapImage.setImageBitmap(bitmap, null, 1, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
