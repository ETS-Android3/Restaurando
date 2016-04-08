package com.kevinlamcs.android.restaurando.utils;

import android.util.Base64;

/**
 * Created by kevin-lam on 4/7/16.
 */
public class DecoderUtils {

    public static String decode(String s, String key) {
        return new String((xorWithKey(Base64.decode(s, Base64.DEFAULT), key.getBytes())));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }
}

