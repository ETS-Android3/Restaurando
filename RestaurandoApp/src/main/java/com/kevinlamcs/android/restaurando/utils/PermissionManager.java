package com.kevinlamcs.android.restaurando.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {

    private static PermissionManager manager;
    private Activity activity;
    private Map<Integer, PermissionAction> actionMap;

    private PermissionManager() {
        this.actionMap = new HashMap<>();
    }

    public static PermissionManager getInstance() {
        if (manager == null) {
            manager = new PermissionManager();
        }
        return manager;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void verifyPermission(String permission, int actionCode) {
        if (hasPermission(permission)) {
            execute(actionCode);
        } else {
            requestPermission(permission, actionCode);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission(String permission, int actionCode) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), actionCode);
    }

    private String[] arrayOf(String permission) {
        return new String[]{permission};
    }

    private void execute(int actionCode) {
        PermissionAction action = actionMap.get(actionCode);
        if (action != null) action.run();
    }

    public void addPermissionAction(int actionCode, PermissionAction action) {
        actionMap.put(actionCode, action);
    }

    public void onRequestPermissionsResult(int actionCode, String[] permissions, int[] grantResults) {
        if (permissionGranted(grantResults)) {
            execute(actionCode);
        }
    }

    private boolean permissionGranted(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}

