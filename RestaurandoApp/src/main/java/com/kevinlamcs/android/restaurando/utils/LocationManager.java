package com.kevinlamcs.android.restaurando.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationManager implements PermissionAction {

    private static LocationManager manager;
    private android.location.LocationManager locationManager;
    private LocationListener locationListener;
    private Location location = null;
    private LocationDependent locationDependent;
    private String provider = android.location.LocationManager.NETWORK_PROVIDER;

    private LocationManager(Context context) {
        locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = setupLocationListener();
    }

    public static LocationManager getInstance(Context context) {
        if (manager == null) {
            manager = new LocationManager(context);
        }
        return manager;
    }

    public void setLocationDependent(LocationDependent locationDependent) {
        this.locationDependent = locationDependent;
    }

    private LocationListener setupLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (LocationManager.this.location == null) {
                    locationDependent.withLocation(asString(location));
                }
                LocationManager.this.location = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    private boolean checkProviderEnabled(String provider) {
        return locationManager.isProviderEnabled(provider);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        if (location == null) {
            requestLocation();
            location = locationManager.getLastKnownLocation(provider);
        }
        if (location != null) {
            locationDependent.withLocation(asString(location));
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        boolean gpsEnabled = checkProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean networkEnabled = checkProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        if (gpsEnabled){
            provider = android.location.LocationManager.GPS_PROVIDER;
        } else if (networkEnabled) {
            provider = android.location.LocationManager.NETWORK_PROVIDER;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
    }

    private String asString(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    public void cancelLocationRequest() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
