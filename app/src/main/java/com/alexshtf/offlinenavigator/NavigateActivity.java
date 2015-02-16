package com.alexshtf.offlinenavigator;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.alexshtf.interp.LocationInterpolator;
import com.alexshtf.interp.Point;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.alexshtf.offlinenavigator.AnchorsManager.isAnchor;
import static com.alexshtf.offlinenavigator.Utils.asPoint;


public class NavigateActivity extends ActionBarActivity {

    public static final String MAP_ID_KEY = "MAP_ID";

    private long mapId;
    private MapsDbOpenHelper mapsDbOpenHelper;

    private MapImageView mapImage;
    private ToggleButton iAmHere;

    private LocationIconPositionManager locationIconPositionManager;
    private LocationInterpolator locationInterpolator;
    private AnchorsManager anchorsManager;
    private GoogleApiClient googleApiClient;

    static void start(Activity parent, long mapId) {
        Intent intent = new Intent(parent, NavigateActivity.class);
        intent.putExtra(MAP_ID_KEY, mapId);
        parent.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mapId = getMapId(getIntent());
        mapsDbOpenHelper = MapsDbOpenHelper.from(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.registerConnectionCallbacks(new ConnectionCallbacks());
        googleApiClient.registerConnectionFailedListener(new LoggingConnectionFailedListener());


        FrameLayout mapLayout = (FrameLayout) findViewById(R.id.map_layout);
        mapImage = (MapImageView) findViewById(R.id.map_image);
        iAmHere = (ToggleButton) findViewById(R.id.i_am_here);

        locationIconPositionManager = new LocationIconPositionManager(mapImage, (ImageView) findViewById(R.id.location_icon));
        locationInterpolator = new LocationInterpolator();
        anchorsManager = new AnchorsManager(this, mapImage, mapLayout, locationInterpolator);

        loadStateFromDatabase();
        enableDisableControls();

        mapImage.setOnTapListener(new ImageTapListener());
        mapImage.setOnRefreshListener(new ImageMatrixChangedListener(
                locationIconPositionManager,
                anchorsManager
        ));
    }

    private static long getMapId(Intent intent) {
        return intent.getLongExtra(MAP_ID_KEY, Long.MAX_VALUE);
    }

    private void loadStateFromDatabase() {
        SQLiteDatabase db = mapsDbOpenHelper.getWritableDatabase();
        try {
            loadMapInfo(db);
            loadAnchors(db);
        }
        finally {
            db.close();
        }
    }

    private void loadMapInfo(SQLiteDatabase db) {
        MapInfo mapInfo = MapsDb.getMap(db, mapId);
        setTitle(mapInfo.getName());
        showImage(mapInfo.getImageUri());
    }

    private void loadAnchors(SQLiteDatabase db) {
        AnchorInfo[] anchors = MapsDb.getAnchors(db, mapId);
        for(AnchorInfo anchorInfo : anchors)
            anchorsManager.addAnchor(anchorInfo);
    }

    private void showImage(String imageUri) {
        mapImage.setImageUri(Uri.parse(imageUri));
    }

    @Override
    protected void onResume() {
        super.onStart();

        Log.i("", "Connecting to Google Location API client");
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        Log.i("", "Disconnecting google Location API client");
        googleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (isAnchor(v)) {
            menu.add(R.string.remove_anchor)
                    .setIcon(android.R.drawable.ic_delete)
                    .setOnMenuItemClickListener(new RemoveAnchorListener(anchorsManager, v))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        else
            super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void enableDisableControls() {
        iAmHere.setEnabled(anchorsManager.canAddAnchor());
    }

    private void userIsHere(float imageX, float imageY) {
        iAmHere.setChecked(false);

        repositionLocationIcon(imageX, imageY);
        AnchorInfo anchorInfo = anchorsManager.addAnchorAtLastKnownLocation(imageX, imageY);
        MapsDb.addAnchor(mapsDbOpenHelper, mapId, anchorInfo);
    }

    private void repositionLocationIcon(float imageX, float imageY) {
        Log.d("", "X = " + imageX + ",  Y = " + imageY);
        locationIconPositionManager.reposition(imageX, imageY);
    }

    private void updateLastLocation(Location location) {
        anchorsManager.updateLocation(location);
        enableDisableControls();
        displayInterpolatedLocation(location);
    }

    private void displayInterpolatedLocation(Location location) {
        Point onImage = locationInterpolator.interpMapToImage(asPoint(location));
        if (onImage != null)
            repositionLocationIcon(onImage.getX(), onImage.getY());
    }

    private class ImageMatrixChangedListener implements MapImageView.IRefreshListener {

        private LocationIconPositionManager locMgr;
        private AnchorsManager anchorsMgr;

        private ImageMatrixChangedListener(LocationIconPositionManager locMgr, AnchorsManager anchorsMgr) {
            this.locMgr = locMgr;
            this.anchorsMgr = anchorsMgr;
        }

        @Override
        public void onRefresh() {
            locMgr.updateDisplay();
            anchorsMgr.updateIconsDisplay();
        }
    }

    private class LocationIconPositionManager {
        private SubsamplingScaleImageView mapImage;
        private ImageView locationIcon;
        private float imageX;
        private float imageY;

        public LocationIconPositionManager(SubsamplingScaleImageView mapImage, ImageView locationIcon) {
            this.mapImage = mapImage;
            this.locationIcon = locationIcon;
        }

        public void reposition(float imageX, float imageY) {
            this.imageX = imageX;
            this.imageY = imageY;
            updateDisplay();
        }

        private void updateDisplay() {
            Utils.repositionIcon(
                    mapImage, locationIcon,
                    imageX, imageY,
                    locationIcon.getWidth(), locationIcon.getHeight()
            );
            locationIcon.setVisibility(View.VISIBLE);
        }
    }

    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
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

    private static class LoggingConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e("", "Google Location API connection failed " + connectionResult.toString());
        }
    }

    private class ImageTapListener implements MapImageView.OnTapListener {
        @Override
        public void onImageTap(View view, float x, float y) {
            if (iAmHere.isChecked()) {
                userIsHere(x, y);
            }
        }
    }

    private class RemoveAnchorListener implements MenuItem.OnMenuItemClickListener {
        private View anchorView;
        private AnchorsManager anchorsManager;

        public RemoveAnchorListener(AnchorsManager anchorsManager, View anchorView) {
            this.anchorView = anchorView;
            this.anchorsManager = anchorsManager;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            MapsDb.removeAnchor(mapsDbOpenHelper, mapId, AnchorsManager.pointOnImageOf(anchorView));
            anchorsManager.removeAnchor(anchorView);
            return true;
        }
    }
}
