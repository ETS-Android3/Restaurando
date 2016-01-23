package com.kevinlamcs.android.restaurando.utils;

/**
 * Created by kevin-lam on 1/19/16.
 */
public class StringManipulationUtils {
    public static final int FOUR_CHARACTERS = 4;
    public static final int REPLACE_SIX = 6;

    public static String replaceImageUrlSize(String url) {
        int urlLength = url.length();

        if (urlLength < FOUR_CHARACTERS) {
            return url;
        }

        return url.substring(0,urlLength - REPLACE_SIX) + "o.jpg";
    }
}
