package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Edman on 1/22/2017.
 */

public class PictureUtils {
    /**
     * Returns a scaled bitmap based on the screen size. It will always be
     * smaller than the screen
     * @param path The path of the original bitmap
     * @param activity The activity that the bitmap will go in
     * @return The scaled bitmap.
     */
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        // get the display size of the device
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    /**
     * Returns a scaled bitmap from the bitmap pointed to by the path.
     * @param path The path to the bitmap to be scaled.
     * @param destWidth The width of the destination bitmap.
     * @param destHeight The height of the destination bitmap.
     * @return Returns a scaled bitmap.
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1; // start with 1-to-1

        // if a src dimension is larger than its dest dimension
        if (srcHeight > destHeight || srcWidth > destWidth) {

            // scale down by the smaller size, because scaling by larger size
            // would leave blank space on the smaller dimension
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }
}
