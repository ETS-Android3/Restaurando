package com.kevinlamcs.android.restaurando.operations;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class that defines vibration detection
 */
public class ShakeDetector implements SensorEventListener {

    private static final int X_INDEX = 0;
    private static final int Y_INDEX = 1;
    private static final int Z_INDEX = 2;

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mOnShakeListener;
    private long mShakeTimeStamp;
    private int mShakeCount;

    public void setOnShakeListener(OnShakeListener listener) {
        mOnShakeListener = listener;
    }

    public interface OnShakeListener {
        void onShake(int count);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mOnShakeListener != null) {
            float x = event.values[X_INDEX];
            float y = event.values[Y_INDEX];
            float z = event.values[Z_INDEX];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                if (mShakeTimeStamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                if (mShakeTimeStamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimeStamp = now;
                mShakeCount++;

                mOnShakeListener.onShake(mShakeCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
