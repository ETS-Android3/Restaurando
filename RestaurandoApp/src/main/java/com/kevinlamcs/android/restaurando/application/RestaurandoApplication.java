package com.kevinlamcs.android.restaurando.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kevinlamcs.android.restaurando.R;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application level class to initialize global settings
 */
public class RestaurandoApplication extends Application {

    private static final String ROBOTO_FONT_PATH = "fonts/Roboto-Regular.ttf";

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Configurating global Roboto font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(ROBOTO_FONT_PATH)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    /**
     * Instantialize tracker for Google Analytics
     * @return Tracker object
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}
