package com.kevinlamcs.android.restaurando.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import static android.support.v4.content.ContextCompat.*;

/**
 * Utility class for determining the user's location.
 */
public class LocationUtils {

    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static String nearbyCoordinates;

    /**
     * Get a user's gps coordinates.
     * @param context - activity context
     * @return nearby coordinates
     */
    public static String getNearbyGPSCoordinates(Context context) {
        boolean gpsEnabled = false, networkEnabled = false;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.getAccuracy() <= 10.0) {
                    nearbyCoordinates = location.getLatitude() + "," +
                            location.getLongitude();
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

        locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
        }

        if (networkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                nearbyCoordinates = location.getLatitude() + "," +
                        location.getLongitude();
            }
        }

        if (gpsEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                nearbyCoordinates = location.getLatitude() + "," + location.getLongitude();
            }
        }

        return nearbyCoordinates;
    }

    /**
     * Disable gps.
     * @param context - activity context
     */
    public static void endGPS(Context context) {
        if (locationManager != null) {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }

    }

    /**
     * Check if there is internet connectivity.
     * @param context - activity context
     * @return true if there is internet. False otherwise
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network network : networks) {
                networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo.isConnected()) {
                    return true;
                }
            }
            return false;
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if (networkInfos != null) {
                    for (NetworkInfo info : networkInfos) {
                        if (info.isConnected()) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }
}
