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


public class CreateMapActivity extends ActionBarActivity {

    public static final String MAP_IMAGE_URI_KEY = "MAP_IMAGE_URI_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);
        showImageFromIntent();
    }

    private void showImageFromIntent() {
        String mapImageFile = getIntent().getStringExtra(MAP_IMAGE_URI_KEY);
        Uri mapImageUri = Uri.parse(mapImageFile);

        ImageView mapImage = (ImageView) findViewById(R.id.map_image_preview);
        mapImage.setImageURI(mapImageUri);
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
            else
                startNavigateActivity(saveMap(mapName, imageUri));

            return true;
        }

        private void startNavigateActivity(long mapId) {
            Intent intent = new Intent(CreateMapActivity.this, NavigateActivity.class);
            intent.putExtra(NavigateActivity.MAP_ID_KEY, mapId);
            startActivity(intent);
            finish();
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
            EditText mapName = (EditText) findViewById(R.id.map_name);
            return mapName.getText().toString().trim();
        }
    }
}
