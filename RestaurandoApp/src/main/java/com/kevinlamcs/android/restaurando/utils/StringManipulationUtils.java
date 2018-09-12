package com.kevinlamcs.android.restaurando.utils;

/**
 * Class for manipulating Strings.
 */
public class StringManipulationUtils {
    private static final int FOUR_CHARACTERS = 4;
    private static final int REPLACE_SIX = 6;

    /**
     * Manipulates the Yelp restaurant image url.
     * @param url - url to manipulate
     * @return new string
     */
    public static String replaceImageUrlSize(String url) {
        int urlLength = url.length();

        if (urlLength < FOUR_CHARACTERS) {
            return url;
        }

        return url.substring(0,urlLength - REPLACE_SIX) + "o.jpg";
    }
}
