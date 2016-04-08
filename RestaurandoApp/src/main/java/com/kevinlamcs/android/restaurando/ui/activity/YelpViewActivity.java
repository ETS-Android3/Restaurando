package com.kevinlamcs.android.restaurando.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;
import com.kevinlamcs.android.restaurando.ui.fragment.YelpViewFragment;

/**
 * Activity class to display the Yelp website inside a webview.
 */
public class YelpViewActivity extends SingleFragmentActivity {

    private YelpViewFragment yelpViewFragment;

    public static Intent newIntent(Context context, String yelpViewUrl) {
        Intent intent = new Intent(context, YelpViewActivity.class);
        intent.setType(yelpViewUrl);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        yelpViewFragment = YelpViewFragment.newInstance(getIntent().getType());
        return yelpViewFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelpview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_yelpview_tool_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if (yelpViewFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
