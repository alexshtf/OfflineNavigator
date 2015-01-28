package com.alexshtf.offlinenavigator;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


public class NavigateActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_FILE_KEY = "MAP_IMAGE_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        showImage();
    }

    private void showImage() {
        String imageFile = getIntent().getStringExtra(MAP_IMAGE_FILE_KEY);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageFile));
            ImageViewTouch mapImage = (ImageViewTouch)findViewById(R.id.map_image);
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
