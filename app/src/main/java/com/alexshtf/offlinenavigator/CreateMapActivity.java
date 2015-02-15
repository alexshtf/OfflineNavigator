package com.alexshtf.offlinenavigator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import uk.co.senab.photoview.PhotoView;


public class CreateMapActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_URI_KEY = "MAP_IMAGE_URI_KEY";
    public static final String MAP_ID_KEY = "MAP_ID_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);
        showImageFromIntent();
    }

    private void showImageFromIntent() {
        String mapImageFile = getIntent().getStringExtra(MAP_IMAGE_URI_KEY);
        Uri mapImageUri = Uri.parse(mapImageFile);

        SubsamplingScaleImageView mapImage = (SubsamplingScaleImageView) findViewById(R.id.map_image_preview);
        mapImage.setImageUri(mapImageUri);
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
