package com.alexshtf.offlinenavigator;

import android.content.Context;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.alexshtf.interp.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.senab.photoview.PhotoView;

import static java.nio.channels.Channels.newChannel;

public class Utils {
    @SafeVarargs
    public static <T> T[] arrayOf(T... items) {
        return items;
    }

    public static String[] stringArrayOf(Object... items) {
        String[] result = new String[items.length];

        for(int i = 0; i < result.length; ++i)
            result[i] = items[i].toString();

        return result;
    }

    public static Point asPoint(Location location) {
        return Point.xy(
                (float) location.getLongitude(),
                (float) location.getLatitude()
        );
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MAP_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public static File createCopyOfImage(Context context, Uri imageUri) throws IOException {
        File outputFile = createImageFile(context);
        copyImage(context, imageUri, outputFile);
        return outputFile;
    }

    private static void copyImage(Context context, Uri imageUri, File outputFile) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        ReadableByteChannel inputChannel = newChannel(inputStream);
        try {
            FileChannel outputChannel = new FileOutputStream(outputFile).getChannel();
            try {
                outputChannel.transferFrom(inputChannel, 0, inputStream.available());
            }
            finally {
                outputChannel.close();
            }
        } finally {
            inputChannel.close();
        }
    }

    public static void deleteMapImage(String imageUri) {
        Uri uri = Uri.parse(imageUri);
        File file = new File(uri.getPath());
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    public static void repositionIcon(PhotoView photoView, View icon, float imageX, float imageY, int iconWidth, int iconHeight) {
        int photoW = photoView.getDrawable().getIntrinsicWidth();
        int photoH = photoView.getDrawable().getIntrinsicHeight();

        float[] xy = {imageX * photoW, imageY * photoH};
        Matrix displayMatrix = photoView.getDisplayMatrix();
        displayMatrix.mapPoints(xy);

        icon.setTranslationX(xy[0] - 0.5f * iconHeight);
        icon.setTranslationY(xy[1] - 0.5f * iconWidth);
    }
}
