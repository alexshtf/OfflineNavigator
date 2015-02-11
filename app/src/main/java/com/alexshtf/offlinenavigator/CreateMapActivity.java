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

    public static final String MAP_IMAGE_FILE_KEY = "MAP_IMAGE_FILE_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);
        showImageFromIntent();
    }

    private void showImageFromIntent() {
        String mapImageFile = getIntent().getStringExtra(MAP_IMAGE_FILE_KEY);
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

            if (mapName.trim().length() == 0)
                notifyMustEnterMapName();
            else
                goToNavigateActivity(mapName);

            return true;
        }

        private void goToNavigateActivity(String mapName) {
            Intent intent = new Intent(CreateMapActivity.this, NavigateActivity.class);
            intent.putExtra(NavigateActivity.MAP_IMAGE_FILE_KEY, getIntent().getStringExtra(MAP_IMAGE_FILE_KEY));
            intent.putExtra(NavigateActivity.MAP_NAME_KEY, mapName);
            startActivity(intent);
            finish();
        }

        private void notifyMustEnterMapName() {
            Toast.makeText(CreateMapActivity.this, R.string.must_enter_map_name, Toast.LENGTH_LONG).show();
        }

        private String getMapName() {
            EditText mapName = (EditText) findViewById(R.id.map_name);
            return mapName.getText().toString();
        }
    }
}
