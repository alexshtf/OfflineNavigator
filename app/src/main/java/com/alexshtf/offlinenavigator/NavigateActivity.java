package com.alexshtf.offlinenavigator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
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
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

//geo fix 32.795083 34.983981

public class NavigateActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_FILE_KEY = "MAP_IMAGE_FILE";

    private ImageViewTouch mapImage;
    private ImageView locationIcon;
    private ToggleButton iAmHere;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mapImage = (ImageViewTouch) findViewById(R.id.map_image);
        locationIcon = (ImageView) findViewById(R.id.location_icon);
        iAmHere = (ToggleButton) findViewById(R.id.i_am_here);

        mapImage.setOnTouchListener(new ImageTapListener());

        enableDisableControls();
        showImageFromIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.registerConnectionCallbacks(new ConnectionCallback());
        googleApiClient.registerConnectionFailedListener(new LoggingConnectionFailedCallback());

        Log.i("", "Connecting to Google Location API client");
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void showImageFromIntent() {
        String imageFile = getIntent().getStringExtra(MAP_IMAGE_FILE_KEY);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageFile));
            mapImage.setImageBitmap(bitmap, null, 1, 10);
        } catch (IOException e) {
            Log.e("", "Unable to read image", e);
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

    private void enableDisableControls() {
        if (lastLocation == null)
            iAmHere.setEnabled(false);
        else
            iAmHere.setEnabled(true);
    }

    private void reportCurrentLocation(float imageX, float imageY) {
        Log.d("", "X = " + imageX + ", Y = " + imageY);
        showCurrentLocation(imageX, imageY);

        if (lastLocation == null)
        {
            Log.d("", "Location: null");
            return;
        }

        float longitude = (float) lastLocation.getLongitude();
        float latitude = (float) lastLocation.getLatitude();

        Log.d("", "Location: " + lastLocation.toString());
        Log.d("", "Location provider: " + lastLocation.getProvider());
        Log.d("", "Location extras: " + lastLocation.getExtras());
        Log.d("", "Longitude: " + longitude);
        Log.d("", "Latitude: " + latitude);

        iAmHere.setChecked(false);
    }

    private void showCurrentLocation(float imageX, float imageY) {
        float[] xy = {imageX, imageY};
        mapImage.getImageViewMatrix().mapPoints(xy);

        locationIcon.setTranslationX(xy[0] - 0.5f * locationIcon.getWidth());
        locationIcon.setTranslationY(xy[1] - 0.5f * locationIcon.getHeight());
    }

    private class ConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle bundle) {
            Log.i("", "Google Location API connected. Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                    new LocationUpdateListener()
            );
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i("", "Google Location API connection suspended");
        }
    }


    private class LocationUpdateListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            enableDisableControls();
        }
    }

    private static class LoggingConnectionFailedCallback implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e("", "Google Location API connection failed " + connectionResult.toString());
        }
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

                reportCurrentLocation(xy[0], xy[1]);
            }

            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return false;
        }
    }
}
