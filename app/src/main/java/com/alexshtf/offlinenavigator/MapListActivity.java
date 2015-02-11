package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MapListActivity extends ActionBarActivity {

    private static final int PICK_IMAGE = 1;
    private MapsDbOpenHelper mapsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        mapsDb = MapsDbOpenHelper.from(this);
    }

    @Override
    protected void onDestroy() {
        mapsDb.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView mapList = (ListView) findViewById(R.id.map_list);
        mapList.setAdapter(new MapListAdapter(mapsDb.getReadableDatabase()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK)
            launchCreateMapActivity(data.getData());

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void launchCreateMapActivity(Uri image) {
        Intent intent = new Intent(this, CreateMapActivity.class);
        intent.putExtra(CreateMapActivity.MAP_IMAGE_URI_KEY, image.toString());
        startActivity(intent);
    }

    private void launchAddExistingImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_map_picture)), PICK_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_add_from_camera:
                Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add_from_gallery:
                launchAddExistingImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MapListAdapter extends CursorAdapter {

        public MapListAdapter(SQLiteDatabase db) {
            super(MapListActivity.this, MapsDb.getAllMaps(db), false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return new TextView(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textView = (TextView) view;
            textView.setText(MapsDb.getMapInfo(cursor).getName());
        }
    }
}
