package com.alexshtf.offlinenavigator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;


public class NavigateActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_FILE_KEY = "MAP_IMAGE_FILE";

    private MatrixNotifyingImageView mapImage;
    private LocationIconPositionManager locationIconPositionManager;
    private ToggleButton iAmHere;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationInterpolator interpolator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mapImage = (MatrixNotifyingImageView) findViewById(R.id.map_image);
        iAmHere = (ToggleButton) findViewById(R.id.i_am_here);

        locationIconPositionManager = new LocationIconPositionManager(mapImage, (ImageView) findViewById(R.id.location_icon));
        mapImage.setSingleTapListener(new ImageTapListener());
        mapImage.setImageMatrixChangedListener(locationIconPositionManager);

        enableDisableControls();
        showImageFromIntent();
        interpolator = new LocationInterpolator();
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

    private void updateIAmHere(float imageX, float imageY) {
        iAmHere.setChecked(false);

        repositionLocationIcon(imageX, imageY);
        updateInterpolator(imageX, imageY);
    }

    private void updateInterpolator(float imageX, float imageY) {
        if (lastLocation == null)
            return;

        float longitude = (float) lastLocation.getLongitude();
        float latitude = (float) lastLocation.getLatitude();
        interpolator.add(new Point(imageX, imageY), new Point(longitude, latitude));
    }

    private void repositionLocationIcon(float imageX, float imageY) {
        Log.d("", "X = " + imageX + ",  Y = " + imageY);
        locationIconPositionManager.reposition(imageX, imageY);
    }

    private void updateLastLocation(Location location) {
        lastLocation = location;
        enableDisableControls();
        displayInterpolatedLocation();
    }

    private void displayInterpolatedLocation() {
        Point approximateOnImage = interpolator.interpolate(Point.xy(
                (float) lastLocation.getLongitude(),
                (float) lastLocation.getLatitude()
        ));

        if (approximateOnImage != null)
            repositionLocationIcon(approximateOnImage.getX(), approximateOnImage.getY());
    }

    private class LocationIconPositionManager implements MatrixNotifyingImageView.ImageMatrixChangedListener {
        private ImageViewTouch mapImage;
        private ImageView locationIcon;
        private float imageX;
        private float imageY;

        public LocationIconPositionManager(ImageViewTouch mapImage, ImageView locationIcon) {
            this.mapImage = mapImage;
            this.locationIcon = locationIcon;
        }

        @Override
        public void onChanged() {
            update();
        }

        public void reposition(float imageX, float imageY) {
            this.imageX = imageX;
            this.imageY = imageY;
            update();
        }

        private void update() {
            float[] xy = {imageX, imageY};
            mapImage.getImageViewMatrix().mapPoints(xy);

            locationIcon.setTranslationX(xy[0] - 0.5f * locationIcon.getWidth());
            locationIcon.setTranslationY(xy[1] - 0.5f * locationIcon.getHeight());
        }
    }

    private class ConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle bundle) {
            Log.i("", "Google Location API connected. Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(5000),
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
            logLocation(location);
            updateLastLocation(location);
        }

        private void logLocation(Location location) {
            Log.d("", "Location: " + location.toString());
            Log.d("", "Location provider: " + location.getProvider());
            Log.d("", "Location extras: " + location.getExtras());
            Log.d("", "Longitude: " + location.getLongitude());
            Log.d("", "Latitude: " + location.getLatitude());
        }
    }

    private static class LoggingConnectionFailedCallback implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e("", "Google Location API connection failed " + connectionResult.toString());
        }
    }

    private class ImageTapListener implements ImageViewTouch.OnImageViewTouchSingleTapListener
    {
        @Override
        public void onSingleTapConfirmed(MotionEvent event) {
            if (iAmHere.isChecked()) {

                Matrix imageViewInv = new Matrix();
                mapImage.getImageViewMatrix().invert(imageViewInv);

                float[] xy = { event.getX(), event.getY() };
                imageViewInv.mapPoints(xy);

                updateIAmHere(xy[0], xy[1]);
            }
        }
    }
}
