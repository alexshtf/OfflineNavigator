package com.alexshtf.offlinenavigator;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static com.alexshtf.offlinenavigator.Utils.arrayOf;


public class CreateMapActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_URI_KEY = "MAP_IMAGE_URI_KEY";
    public static final String MAP_ID_KEY = "MAP_ID_KEY";

    private MapImageView mapImage;
    private ImageView topLeftIcon;
    private ImageView topRightIcon;
    private ImageView bottomLeftIcon;
    private ImageView bottomRightIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);
        findViews();
        hideCropViews();
        showImageFromIntent();
        initializeCropIconsAfterImageReady();
    }

    private void hideCropViews() {
        for(View icon : getIconViews())
            icon.setVisibility(View.INVISIBLE);
    }

    private void showImageFromIntent() {
        String mapImageFile = getIntent().getStringExtra(MAP_IMAGE_URI_KEY);
        Uri mapImageUri = Uri.parse(mapImageFile);
        mapImage.setImageUri(mapImageUri);
    }

    private void findViews() {
        mapImage = (MapImageView) findViewById(R.id.map_image_preview);
        topLeftIcon = (ImageView) findViewById(R.id.top_left);
        topRightIcon = (ImageView) findViewById(R.id.top_right);
        bottomLeftIcon = (ImageView) findViewById(R.id.bottom_left);
        bottomRightIcon = (ImageView) findViewById(R.id.bottom_right);
    }

    private void initializeCropIconsAfterImageReady() {
        mapImage.setOnImageReadyListener(new MapImageView.OnImageReadyListener() {
            @Override
            public void onImageReady() {
                setupIconViews();
                showIconViews();
                setupOnRefreshListener();
                mapImage.setOnImageReadyListener(null);
            }
        });
    }

    private void showIconViews() {
        for(View icon : getIconViews())
            icon.setVisibility(View.VISIBLE);
    }

    private void setupOnRefreshListener() {
        mapImage.setOnRefreshListener(new MapImageView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshIconPositions();
            }
        });
    }

    private void setupIconViews() {
        int w = mapImage.getSWidth();
        int h = mapImage.getSHeight();
        setIconPosition(topLeftIcon, 0, 0);
        setIconPosition(topRightIcon, w, 0);
        setIconPosition(bottomRightIcon, w, h);
        setIconPosition(bottomLeftIcon, 0, h);
        refreshIconPositions();
    }

    private void setIconPosition(ImageView icon, float x, float y) {
        icon.setTag(new PointF(x, y));
    }

    private PointF getIconPosition(ImageView icon) {
        return (PointF) icon.getTag();
    }

    private void refreshIconPositions() {
        for(ImageView icon : getIconViews())
            repositionIcon(icon, getIconPosition(icon).x, getIconPosition(icon).y);
    }

    private ImageView[] getIconViews() {
        return arrayOf(topLeftIcon, topRightIcon, bottomLeftIcon, bottomRightIcon);
    }

    private void repositionIcon(ImageView icon, float x, float y) {
        Utils.repositionIcon(mapImage, icon, x, y, icon.getDrawable().getIntrinsicWidth(), icon.getDrawable().getIntrinsicHeight());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.save_map)
                .setIcon(android.R.drawable.ic_input_add)
                .setOnMenuItemClickListener(new SaveMapListener())
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    private class SaveMapListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            String mapName = getMapName();
            String imageUri = getIntent().getStringExtra(MAP_IMAGE_URI_KEY);

            if (mapName.isEmpty())
                notifyMustEnterMapName();
            else {
                Intent result = new Intent();
                result.putExtra(MAP_ID_KEY, saveMap(mapName, imageUri));
                setResult(RESULT_OK, result);
                finish();
            }

            return true;
        }

        private long saveMap(String mapName, String imageUri) {
            MapsDbOpenHelper dbOpenHelper = MapsDbOpenHelper.from(CreateMapActivity.this);
            try {
                return MapsDb.addMap(dbOpenHelper, mapName, imageUri);
            }
            finally {
                dbOpenHelper.close();
            }
        }

        private void notifyMustEnterMapName() {
            Toast.makeText(CreateMapActivity.this, R.string.must_enter_map_name, Toast.LENGTH_LONG).show();
        }

        private String getMapName() {
            EditText mapName = (EditText) findViewById(R.id.map_name_view);
            return mapName.getText().toString().trim();
        }
    }

}
