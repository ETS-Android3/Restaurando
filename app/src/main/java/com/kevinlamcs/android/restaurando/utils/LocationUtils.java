package com.kevinlamcs.android.restaurando.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import static android.support.v4.content.ContextCompat.*;

/**
 * Created by kevin-lam on 1/15/16.
 */
public class LocationUtils {
    private static LocationManager sLocationManager;
    private static LocationListener sLocationListener;
    private static String sNearbyCoordinates;

    public static String getNearbyGPSCoordinates(Context context) {
        boolean gpsEnabled = false, networkEnabled = false;
        sLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.getAccuracy() <= 10.0) {
                    sNearbyCoordinates = location.getLatitude() + "," + location
                            .getLongitude();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        ;

        sLocationManager = (LocationManager) context.getSystemService(Context
                .LOCATION_SERVICE);

        try {
            gpsEnabled = sLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }

        try {
            networkEnabled = sLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
        }

        if (networkEnabled) {
            sLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, sLocationListener);
            Location location = sLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                sNearbyCoordinates = location.getLatitude() + "," + location
                        .getLongitude();
            }
        }

        if (gpsEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
            }

            sLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    sLocationListener);
            Location location = sLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                sNearbyCoordinates = location.getLatitude() + "," + location.getLongitude();
            }
        }

        return sNearbyCoordinates;
    }

    public static void endGPS(Context context) {
        if (sLocationManager != null) {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            sLocationManager.removeUpdates(sLocationListener);
            sLocationManager = null;
        }

    }
}
