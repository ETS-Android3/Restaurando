package com.kevinlamcs.android.restaurando.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectionManager {

    private static ConnectionManager manager;
    private final Context context;

    private ConnectionManager(Context context) {
        this.context = context;
    }

    public static ConnectionManager getInstance(Context context) {
        if (manager == null) {
            manager = new ConnectionManager(context);
        }
        return manager;
    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (buildVersionSatisfied()) {
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

    private static boolean buildVersionSatisfied() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
