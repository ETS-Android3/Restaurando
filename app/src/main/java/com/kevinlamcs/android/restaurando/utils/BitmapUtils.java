package com.kevinlamcs.android.restaurando.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

/**
 * Created by kevin-lam on 1/19/16.
 */
public class BitmapUtils {
    public static Bitmap scaleBitmap(Bitmap bitmap, Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int scaledWidth = displayMetrics.widthPixels;
        double scaledHeight = displayMetrics.heightPixels / 2;

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, (int)scaledHeight, true);
    }
}
