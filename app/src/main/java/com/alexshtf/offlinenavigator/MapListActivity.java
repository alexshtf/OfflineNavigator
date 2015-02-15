package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;

import static com.alexshtf.offlinenavigator.Utils.createCopyOfImage;


public class MapListActivity extends ActionBarActivity {

    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;

    private MapsDbOpenHelper mapsDb;
    private MapListAdapter listAdapter;
    private File photoFile;

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
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photoFile = (File) savedInstanceState.getSerializable("photo_file");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("photo_file", photoFile);
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

    private void launchAddFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Log.e("", "Image capture is not supported. resolveActivity returned null");
            Toast.makeText(this, R.string.image_capture_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        photoFile = null;
        try {
            photoFile = Utils.createImageFile(this);
        } catch (IOException e) {
            Log.e("", "Cannot create image file", e);
            Toast.makeText(this, R.string.cannot_create_image_file, Toast.LENGTH_SHORT).show();
            return;
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, CAPTURE_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                File imageFile = createCopyOfImage(this, data.getData());
                launchCreateMapActivity(Uri.fromFile(imageFile));
            } catch (IOException e) {
                Log.e("", "Cannot copy image to private storage", e);
            }
        }

        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK)
                launchCreateMapActivity(Uri.fromFile(photoFile));
            else
                //noinspection ResultOfMethodCallIgnored
                photoFile.delete();
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
                launchAddFromCamera();
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
            final View view = LayoutInflater.from(context).inflate(R.layout.map_list_item, parent, false);

            view.setTag(new MapItemTag(
                    (TextView) view.findViewById(R.id.map_name_view)
            ));

            view.findViewById(R.id.delete_map_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMapImage(view);
                    deleteMapFromDb(view);
                    refreshMapsFromDb();
                }
            });

            bindView(view, context, cursor);

            return view;
        }

        private void refreshMapsFromDb() {
            changeCursor(MapsDb.getAllMaps(db));
        }

        private void deleteMapFromDb(View view) {
            long mapId = getTag(view).getMapId();
            MapsDb.deleteMap(mapsDb, mapId);
        }

        private void deleteMapImage(View view) {
            String imageUri = getTag(view).getMapInfo().getImageUri();
            Utils.deleteMapImage(imageUri);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            MapInfo mapInfo = MapsDb.getMapInfo(cursor);

            MapItemTag tag = getTag(view);
            tag.setMapInfo(mapInfo);
            tag.setMapId(cursor.getLong(cursor.getColumnIndex("_id")));
            tag.getMapNameView().setText(mapInfo.getName());
        }

        MapItemTag getTag(View view) {
            return (MapItemTag) view.getTag();
        }
    }

    private static class MapItemTag {
        private final TextView mapNameView;
        private MapInfo mapInfo;
        private long mapId;

        public MapItemTag(TextView mapNameView) {
            this.mapNameView = mapNameView;
        }

        public TextView getMapNameView() {
            return mapNameView;
        }

        public MapInfo getMapInfo() {
            return mapInfo;
        }

        public void setMapInfo(MapInfo mapInfo) {
            this.mapInfo = mapInfo;
        }

        public long getMapId() { return mapId; }

        public void setMapId(long mapId) { this.mapId = mapId; }
    }
}
