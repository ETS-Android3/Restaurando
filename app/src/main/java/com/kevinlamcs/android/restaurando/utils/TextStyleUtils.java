package com.kevinlamcs.android.restaurando.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.SearchView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by kevin-lam on 1/13/16.
 */
public class TextStyleUtils {

    public static void setFavoritesListTextStyle(Context context, TextView textView, String path) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);
        textView.setTypeface(typeface);
    }
}
