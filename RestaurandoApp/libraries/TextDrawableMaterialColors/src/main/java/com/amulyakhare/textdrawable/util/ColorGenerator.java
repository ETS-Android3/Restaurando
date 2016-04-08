package com.amulyakhare.textdrawable.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author amulya
 * @datetime 14 Oct 2014, 5:20 PM
 */
public class ColorGenerator {

    public static ColorGenerator DEFAULT;

    public static ColorGenerator MATERIAL;

    static {
        DEFAULT = create(Arrays.asList(
                0xfff16364,
                0xfff58559,
                0xfff9a43e,
                0xffe4c62e,
                0xff67bf74,
                0xff59a2be,
                0xff2093cd,
                0xffad62a7,
                0xff805781
        ));
        MATERIAL = create(Arrays.asList(
                0xffef9a9a,
                0xffef5350,
                0xfff48fb1,
                0xffec407a,
                0xffce93d8,
                0xffab47bc,
                0xffb39ddb,
                0xff7e57c2,
                0xff9fa8da,
                0xff5c6bc0,
                0xff90caf9,
                0xff42a5f5,
                0xff81d4fa,
                0xff29b6f6,
                0xff80deea,
                0xff26c6da,
                0xff80cbc4,
                0xff26a69a,
                0xffa5d6a7,
                0xff66bb6a,
                0xffc5e1a5,
                0xff9ccc65,
                0xffe6ee9c,
                0xffd4e157,
                0xfffff59d,
                0xffffee58,
                0xffffe082,
                0xffffca28,
                0xffffcc80,
                0xffffa726,
                0xffffab91,
                0xffff7043
        ));
    }

    private final List<Integer> mColors;
    private final Random mRandom;

    public static ColorGenerator create(List<Integer> colorList) {
        return new ColorGenerator(colorList);
    }

    private ColorGenerator(List<Integer> colorList) {
        mColors = colorList;
        mRandom = new Random(System.currentTimeMillis());
    }

    public int getRandomColor() {
        return mColors.get(mRandom.nextInt(mColors.size()));
    }

    public int getColor(Object key) {
        return mColors.get(Math.abs(key.hashCode()) % mColors.size());
    }
}
