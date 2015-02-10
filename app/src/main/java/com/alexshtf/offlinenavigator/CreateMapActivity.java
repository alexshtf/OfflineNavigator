package com.alexshtf.offlinenavigator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


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
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    private class SaveMapListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = new Intent(CreateMapActivity.this, NavigateActivity.class);
            intent.putExtra(NavigateActivity.MAP_IMAGE_FILE_KEY, getIntent().getStringExtra(MAP_IMAGE_FILE_KEY));
            startActivity(intent);
            return true;
        }
    }
}
