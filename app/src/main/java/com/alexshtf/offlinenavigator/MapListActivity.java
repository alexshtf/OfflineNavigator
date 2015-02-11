package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MapListActivity extends ActionBarActivity {

    private static final int PICK_IMAGE = 1;
    private MapsDbOpenHelper mapsDb;
    private MapListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        mapsDb = MapsDbOpenHelper.from(this);
        listAdapter = new MapListAdapter(mapsDb.getReadableDatabase());

        ListView mapList = (ListView) findViewById(R.id.map_list);
        mapList.setAdapter(listAdapter);
        mapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavigateActivity.start(MapListActivity.this, id);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mapsDb.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.notifyDataSetInvalidated();
    }

    private void launchAddExistingImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        else
            intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_map_picture)), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(
                        data.getData(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            }
            launchCreateMapActivity(data.getData());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void launchCreateMapActivity(Uri image) {
        Intent intent = new Intent(this, CreateMapActivity.class);
        intent.putExtra(CreateMapActivity.MAP_IMAGE_URI_KEY, image.toString());
        startActivity(intent);
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

        private final SQLiteDatabase db;

        public MapListAdapter(SQLiteDatabase db) {
            super(MapListActivity.this, MapsDb.getAllMaps(db), false);
            this.db = db;
        }

        @Override
        public View newView(Context context, final Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.map_list_item, parent, false);

            view.setTag(new MapItemTag(
                    (TextView) view.findViewById(R.id.map_name_view)
            ));

            final long mapId = cursor.getLong(cursor.getColumnIndex("_id"));
            view.findViewById(R.id.delete_map_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapsDb.deleteMap(mapsDb, mapId);
                    changeCursor(MapsDb.getAllMaps(db));
                }
            });

            bindView(view, context, cursor);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            MapItemTag tag = (MapItemTag) view.getTag();
            tag.getMapNameView().setText(MapsDb.getMapInfo(cursor).getName());
        }
    }

    private static class MapItemTag {
        private final TextView mapNameView;

        public MapItemTag(TextView mapNameView) {
            this.mapNameView = mapNameView;
        }

        public TextView getMapNameView() {
            return mapNameView;
        }
    }
}
